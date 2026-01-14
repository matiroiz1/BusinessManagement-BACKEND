package com.example.backgestcom.inventory.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Request to adjust stock for a product in the default deposit (MVP).
 *
 * - direction = "IN" adds quantity to on-hand stock
 * - direction = "OUT" subtracts quantity from on-hand stock (cannot go negative)
 *
 * reason/notes are stored in StockMovement for traceability (kardex).
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
    private String reason;    // e.g. "SALE_CONFIRMATION", "MANUAL_ADJUSTMENT", "PURCHASE_RECEIPT"

    private String notes;     // optional extra context (saleId, ticket number, etc.)

    private UUID performedByUserId;
}
