package com.example.BACKGestCom.inventory.api.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for the POST /api/inventory/stock/adjust endpoint.
 * * Purpose: Represents a manual stock adjustment (increase or decrease).
 * It carries the quantity, direction (IN/OUT), and the reason for the change.
 * * The service layer uses this data to:
 * 1. Update the StockItem.onHand balance.
 * 2. Create a StockMovement record with ADJUSTMENT_IN/OUT types for full traceability.
 */
@Data
public class AdjustStockRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal quantity;

    @NotBlank
    private String direction; // "IN" or "OUT"

    @NotBlank
    private String reason;

    private String notes;
}
