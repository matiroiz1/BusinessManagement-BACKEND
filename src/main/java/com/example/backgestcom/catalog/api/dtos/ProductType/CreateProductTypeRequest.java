package com.example.backgestcom.catalog.api.dtos.ProductType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductTypeRequest {
    @NotBlank @Size(max = 80)
    private String name;
    private String description;
}
