package com.example.backgestcom.inventory.api.dtos;

import com.example.backgestcom.inventory.domain.StockMovementType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * DTO for GET endpoints like /api/inventory/movements/by-product/{productId}.
 * * Purpose: Represents the inventory "Kardex" or transaction history.
 * Provides a detailed list of movements with audit fields such as timestamp,
 * movement type, quantity, reference, and reason.
 * * It allows users to audit and explain the evolution of stock levels over time.
 */
@Value
@Builder
public class StockMovementResponse {
    UUID id;
    Instant createdAt;
    StockMovementType type;
    UUID productId;
    UUID depositId;
    BigDecimal quantity;
    String referenceType;
    UUID referenceId;
    String reason;
    String notes;
}
