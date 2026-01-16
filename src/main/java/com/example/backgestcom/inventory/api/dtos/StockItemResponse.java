package com.example.backgestcom.inventory.api.dtos;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for GET endpoints like /api/inventory/stock/by-product/{productId}.
 * * Purpose: Returns a snapshot of the current stock status.
 * Includes current on-hand levels, thresholds, and calculated status (OK/LOW/CRITICAL).
 * * This DTO prevents the direct exposure of the StockItem entity, avoiding potential
 * issues with LAZY loading and internal database relationships.
 */
@Value
@Builder
public class StockItemResponse {
    UUID id;
    UUID productId;
    UUID depositId;
    BigDecimal onHand;
    BigDecimal criticalThreshold;
    BigDecimal minimumThreshold;
    String productName;
    String stockState;     // CRITICAL / LOW / OK (computed)
    Instant updatedAt;
}
