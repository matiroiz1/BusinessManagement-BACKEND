package com.example.backgestcom.sales.application;

import com.example.backgestcom.catalog.infra.ProductRepository;
import com.example.backgestcom.inventory.application.InventoryService;
import com.example.backgestcom.sales.api.dtos.*;
import com.example.backgestcom.sales.domain.*;
import com.example.backgestcom.sales.infra.SaleRepository;
import com.example.backgestcom.security.application.CurrentUser;
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
 * Sales use-cases:
 * - Create draft sale
 * - Confirm sale (freeze prices, compute totals, reduce stock, create stock movements)
 */
@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CurrentUser currentUser;
    private final InventoryService inventoryService;

    @Transactional
    public SaleResponse createDraft(CreateSaleRequest req) {
        UUID soldByUserId = currentUser.id();

        Sale sale = new Sale();
        sale.setStatus(SaleStatus.PENDING);
        sale.setSoldByUserId(soldByUserId);
        sale.setCustomerName(req.getCustomerName());
        sale.setCustomerDocument(req.getCustomerDocument());
        sale.setNotes(req.getNotes());
        sale.setCreatedAt(Instant.now());
        sale.setUpdatedAt(Instant.now());

        // Create items (pricing will be set at confirm time)
        for (CreateSaleItemRequest itemReq : req.getItems()) {
            var product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productId: " + itemReq.getProductId()));

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setDiscountAmount(nvl(itemReq.getDiscountAmount()));

            // draft placeholders
            item.setUnitPrice(BigDecimal.ZERO);
            item.setLineSubtotal(BigDecimal.ZERO);
            item.setLineTotal(BigDecimal.ZERO);

            sale.getItems().add(item);
        }

        // draft totals placeholders
        sale.setSubtotal(BigDecimal.ZERO);
        sale.setTotalDiscount(BigDecimal.ZERO);
        sale.setTotal(BigDecimal.ZERO);

        sale = saleRepository.save(sale);
        return toResponse(sale);
    }
    //----------------------------------------------------------------------------------
    /**
     * Confirm sale:
     * - freeze unit prices from Product.currentSalePrice into SaleItem.unitPrice
     * - compute subtotals/totals
     * - reduce stock via inventory module (OUT adjustment) and register kardex movements
     */
    @Transactional
    public SaleResponse confirm(UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found"));

        if (sale.getStatus() != SaleStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale is not in PENDING status");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (SaleItem item : sale.getItems()) {
            var product = item.getProduct();

            // freeze price now
            BigDecimal unitPrice = nvl(product.getCurrentSalePrice());
            item.setUnitPrice(unitPrice);

            BigDecimal qty = item.getQuantity();
            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid quantity");
            }

            BigDecimal lineSubtotal = unitPrice.multiply(qty);
            BigDecimal discount = nvl(item.getDiscountAmount());
            if (discount.compareTo(lineSubtotal) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Discount cannot exceed line subtotal");
            }

            BigDecimal lineTotal = lineSubtotal.subtract(discount);

            item.setLineSubtotal(lineSubtotal);
            item.setLineTotal(lineTotal);

            subtotal = subtotal.add(lineSubtotal);
            totalDiscount = totalDiscount.add(discount);
        }

        BigDecimal total = subtotal.subtract(totalDiscount);

        // update header totals
        sale.setSubtotal(subtotal);
        sale.setTotalDiscount(totalDiscount);
        sale.setTotal(total);

        // reduce stock for each item
        for (SaleItem item : sale.getItems()) {
            inventoryService.applySaleOut(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    sale.getId(),
                    sale.getSoldByUserId()
            );
        }

        sale.setStatus(SaleStatus.CONFIRMED);
        sale.setConfirmedAt(Instant.now());
        sale.setUpdatedAt(Instant.now());

        sale = saleRepository.save(sale);
        return toResponse(sale);
    }
    //----------------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public SaleResponse getById(UUID saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found"));
        return toResponse(sale);
    }
    //----------------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<SaleResponse> list() {
        return saleRepository.findAll().stream().map(this::toResponse).toList();
    }
    //----------------------------------------------------------------------------------
    private SaleResponse toResponse(Sale sale) {
        return SaleResponse.builder()
                .id(sale.getId())
                .status(sale.getStatus())
                .soldByUserId(sale.getSoldByUserId())
                .customerName(sale.getCustomerName())
                .customerDocument(sale.getCustomerDocument())
                .subtotal(sale.getSubtotal())
                .totalDiscount(sale.getTotalDiscount())
                .total(sale.getTotal())
                .notes(sale.getNotes())
                .createdAt(sale.getCreatedAt())
                .confirmedAt(sale.getConfirmedAt())
                .items(sale.getItems().stream().map(i -> SaleItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .quantity(i.getQuantity())
                        .unitPrice(i.getUnitPrice())
                        .discountAmount(i.getDiscountAmount())
                        .lineSubtotal(i.getLineSubtotal())
                        .lineTotal(i.getLineTotal())
                        .build()).toList())
                .build();
    }
    //----------------------------------------------------------------------------------
    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
