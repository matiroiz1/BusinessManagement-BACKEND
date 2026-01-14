package com.example.backgestcom.sales.api.dtos;

import com.example.backgestcom.sales.domain.SaleStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Sale API response (header + items).
 */
@Value
@Builder
public class SaleResponse {
    UUID id;
    SaleStatus status;

    UUID soldByUserId;

    String customerName;
    String customerDocument;

    BigDecimal subtotal;
    BigDecimal totalDiscount;
    BigDecimal total;

    String notes;

    Instant createdAt;
    Instant confirmedAt;

    List<SaleItemResponse> items;
}
