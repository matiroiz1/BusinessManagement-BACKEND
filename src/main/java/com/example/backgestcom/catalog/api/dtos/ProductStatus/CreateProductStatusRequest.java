package com.example.backgestcom.catalog.api.dtos.ProductStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductStatusRequest {
    @NotBlank
    @Size(max = 80)
    private String name;

    private boolean allowsSales = true;
    private boolean allowsPurchases = true;
}