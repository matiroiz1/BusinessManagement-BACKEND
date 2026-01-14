package com.example.backgestcom.inventory.api;

import com.example.backgestcom.inventory.api.dtos.*;
import com.example.backgestcom.inventory.api.dtos.AdjustStockRequest;
import com.example.backgestcom.inventory.application.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Inventory REST endpoints for the MVP (single deposit).
 * Controllers are thin: they validate input and delegate to services.
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Initializes stock for a product in the default deposit.
     * Use this once per product (MVP).
     */
    @PostMapping("/stock/initialize")
    @ResponseStatus(HttpStatus.CREATED)
    public StockItemResponse initialize(@RequestBody @Valid InitializeStockRequest req) {
        return inventoryService.initializeStock(req);
    }

    /**
     * Manual stock adjustment (IN/OUT).
     * OUT adjustments will fail if stock becomes negative.
     */
    @PostMapping("/stock/adjust")
    public StockItemResponse adjust(@RequestBody @Valid AdjustStockRequest req) {
        return inventoryService.adjustStock(req);
    }

    /**
     * Returns current stock for a product in the default deposit.
     */
    @GetMapping("/stock/by-product/{productId}")
    public StockItemResponse getStock(@PathVariable UUID productId) {
        return inventoryService.getStockByProduct(productId);
    }

    /**
     * Returns last 200 stock movements (kardex) for a product in the default deposit.
     */
    @GetMapping("/movements/by-product/{productId}")
    public List<StockMovementResponse> movements(@PathVariable UUID productId) {
        return inventoryService.getMovements(productId);
    }
}
