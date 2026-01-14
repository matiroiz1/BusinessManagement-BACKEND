package com.example.BACKGestCom.inventory.api.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for the POST /api/inventory/stock/initialize endpoint.
 * * Purpose: Handles the initial setup of a StockItem for a product in a specific deposit.
 * It captures the minimum required data to bootstrap inventory, including initial on-hand
 * quantities and safety thresholds.
 * * Validates the payload using @NotNull, @DecimalMin, and other constraints to ensure
 * data integrity before processing.
 */
@Data
public class InitializeStockRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal initialOnHand;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal criticalThreshold;

    @DecimalMin("0.00")
    private BigDecimal minimumThreshold;
}
