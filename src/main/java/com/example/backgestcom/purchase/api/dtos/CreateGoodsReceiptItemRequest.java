package com.example.backgestcom.purchase.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateGoodsReceiptItemRequest {
    @NotNull
    private UUID productId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal quantity;
}
