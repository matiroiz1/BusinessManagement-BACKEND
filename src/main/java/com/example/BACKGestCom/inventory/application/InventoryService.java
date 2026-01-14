package com.example.BACKGestCom.inventory.application;

import com.example.BACKGestCom.inventory.api.dtos.*;
import com.example.BACKGestCom.inventory.domain.*;
import com.example.BACKGestCom.inventory.infra.DepositRepository;
import com.example.BACKGestCom.inventory.infra.StockItemRepository;
import com.example.BACKGestCom.inventory.infra.StockMovementRepository;
import com.example.BACKGestCom.catalog.infra.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service layer handling inventory business logic.
 * Orchestrates stock initialization, manual adjustments, and audit traceability.
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final DepositRepository depositRepository;
    private final ProductRepository productRepository;
    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;

    /**
     * Retrieves the default active deposit for the current operation.
     * @return The first active Deposit found in the system.
     * @throws ResponseStatusException 400 if no active deposit is configured.
     */
    @Transactional(readOnly = true)
    public Deposit getDefaultDeposit() {
        return depositRepository.findAll().stream()
                .filter(Deposit::isActive)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No active deposit configured"));
    }

    /**
     * Creates the initial stock record (StockItem) for a product.
     * Also records an initial movement in the history (Kardex).
     * @param req Data containing productId, initial stock, and safety thresholds.
     * @return DTO representation of the newly created StockItem.
     */
    @Transactional
    public StockItemResponse initializeStock(InitializeStockRequest req) {
        Deposit deposit = getDefaultDeposit();
        var product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productId"));

        // Validation: Prevent duplicate initialization for the same product/deposit pair
        stockItemRepository.findByProduct_IdAndDeposit_Id(product.getId(), deposit.getId())
                .ifPresent(si -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock already initialized"); });

        // 1. Create and save the "On Hand" balance entity
        StockItem si = StockItem.builder()
                .product(product)
                .deposit(deposit)
                .onHand(req.getInitialOnHand())
                .criticalThreshold(req.getCriticalThreshold())
                .minimumThreshold(req.getMinimumThreshold())
                .updatedAt(Instant.now())
                .build();

        si = stockItemRepository.save(si);

        // 2. Register the first movement to ensure traceability from day zero
        StockMovement movement = StockMovement.builder()
                .type(StockMovementType.ADJUSTMENT_IN)
                .product(product)
                .deposit(deposit)
                .quantity(req.getInitialOnHand())
                .referenceType("INITIALIZATION")
                .reason("Initial stock load")
                .notes(null)
                .build();
        stockMovementRepository.save(movement);

        return toResponse(si);
    }

    /**
     * Updates stock levels based on manual IN/OUT adjustments.
     * Validates that stock does not fall below zero for outgoing movements.
     * @param req Data containing productId, adjustment quantity, and direction (IN/OUT).
     * @return Updated StockItem snapshot.
     */
    @Transactional
    public StockItemResponse adjustStock(AdjustStockRequest req) {
        Deposit deposit = getDefaultDeposit();
        var product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productId"));

        StockItem si = stockItemRepository.findByProduct_IdAndDeposit_Id(product.getId(), deposit.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock not initialized for this product"));

        BigDecimal qty = req.getQuantity();
        boolean isIn = "IN".equalsIgnoreCase(req.getDirection());
        boolean isOut = "OUT".equalsIgnoreCase(req.getDirection());

        if (!isIn && !isOut) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be IN or OUT");
        }

        // Logic for stock reduction
        if (isOut) {
            BigDecimal newOnHand = si.getOnHand().subtract(qty);
            if (newOnHand.compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock (negative stock not allowed)");
            }
            si.setOnHand(newOnHand);
        } else {
            // Logic for stock increase
            si.setOnHand(si.getOnHand().add(qty));
        }

        si.setUpdatedAt(Instant.now());
        si = stockItemRepository.save(si);

        // Record the adjustment in the movement history for auditing
        StockMovement movement = StockMovement.builder()
                .type(isIn ? StockMovementType.ADJUSTMENT_IN : StockMovementType.ADJUSTMENT_OUT)
                .product(product)
                .deposit(deposit)
                .quantity(qty)
                .referenceType("MANUAL_ADJUSTMENT")
                .reason(req.getReason())
                .notes(req.getNotes())
                .build();
        stockMovementRepository.save(movement);

        return toResponse(si);
    }

    /**
     * Fetches the current stock balance for a specific product in the default deposit.
     */
    @Transactional(readOnly = true)
    public StockItemResponse getStockByProduct(UUID productId) {
        Deposit deposit = getDefaultDeposit();
        var si = stockItemRepository.findByProduct_IdAndDeposit_Id(productId, deposit.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));
        return toResponse(si);
    }

    /**
     * Retrieves the audit trail (Kardex) for a product, showing the last 200 movements.
     */
    @Transactional(readOnly = true)
    public List<StockMovementResponse> getMovements(UUID productId) {
        Deposit deposit = getDefaultDeposit();
        return stockMovementRepository
                .findTop200ByProduct_IdAndDeposit_IdOrderByCreatedAtDesc(productId, deposit.getId())
                .stream()
                .map(m -> StockMovementResponse.builder()
                        .id(m.getId())
                        .createdAt(m.getCreatedAt())
                        .type(m.getType())
                        .productId(m.getProduct().getId())
                        .depositId(m.getDeposit().getId())
                        .quantity(m.getQuantity())
                        .referenceType(m.getReferenceType())
                        .referenceId(m.getReferenceId())
                        .reason(m.getReason())
                        .notes(m.getNotes())
                        .build())
                .toList();
    }

    /**
     * Helper method to map a StockItem entity to its corresponding Response DTO.
     * Calculates the visual stock state (CRITICAL/LOW/OK).
     */
    private StockItemResponse toResponse(StockItem si) {
        String state = computeState(si.getOnHand(), si.getCriticalThreshold(), si.getMinimumThreshold());
        return StockItemResponse.builder()
                .id(si.getId())
                .productId(si.getProduct().getId())
                .depositId(si.getDeposit().getId())
                .onHand(si.getOnHand())
                .criticalThreshold(si.getCriticalThreshold())
                .minimumThreshold(si.getMinimumThreshold())
                .stockState(state)
                .updatedAt(si.getUpdatedAt())
                .build();
    }

    /**
     * Logic to determine stock health based on current thresholds.
     */
    private String computeState(BigDecimal onHand, BigDecimal critical, BigDecimal minimum) {
        if (onHand.compareTo(critical) <= 0) return "CRITICAL";
        if (minimum != null && onHand.compareTo(minimum) <= 0) return "LOW";
        return "OK";
    }
}