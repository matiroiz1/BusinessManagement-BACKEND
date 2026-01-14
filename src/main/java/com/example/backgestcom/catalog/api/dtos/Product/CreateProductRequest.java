package com.example.backgestcom.catalog.api.dtos.Product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @NotBlank @Size(max = 60)
    private String sku;

    @NotBlank @Size(max = 160)
    private String name;

    private String description;

    @Size(max = 80)
    private String barcode;

    @NotNull
    private UUID productTypeId;

    @NotNull
    private UUID productStatusId;

    @NotNull
    private UUID unitMeasureId;

    @NotNull @DecimalMin("0.00")
    private BigDecimal currentSalePrice;

    @DecimalMin("0.00")
    private BigDecimal currentCost;

    private Boolean active = true;
}