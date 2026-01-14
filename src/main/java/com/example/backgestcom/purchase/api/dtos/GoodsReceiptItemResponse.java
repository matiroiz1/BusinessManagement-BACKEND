package com.example.backgestcom.purchase.api.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class GoodsReceiptItemResponse {
    private UUID id;
    private UUID productId;
    private BigDecimal quantity;
}
