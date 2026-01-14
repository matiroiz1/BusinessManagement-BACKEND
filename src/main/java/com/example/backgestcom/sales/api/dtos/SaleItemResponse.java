package com.example.backgestcom.sales.api.dtos;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Sale line response with frozen pricing.
 */
@Value
@Builder
public class SaleItemResponse {
    UUID id;
    UUID productId;

    BigDecimal quantity;
    BigDecimal unitPrice;
    BigDecimal discountAmount;

    BigDecimal lineSubtotal;
    BigDecimal lineTotal;
}
