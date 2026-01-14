package com.example.backgestcom.sales.api.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Line item input. Unit price is not accepted from client to avoid tampering;
 * it will be taken from Product.currentSalePrice when confirming (frozen snapshot).
 */
@Data
public class CreateSaleItemRequest {

    @NotNull
    private UUID productId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal quantity;

    // Optional future use: allow discount at item creation (still validated in service)
    @DecimalMin("0.00")
    private BigDecimal discountAmount = BigDecimal.ZERO;
}
