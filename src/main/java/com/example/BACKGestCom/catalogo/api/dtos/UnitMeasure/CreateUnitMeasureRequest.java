package com.example.BACKGestCom.catalogo.api.dtos.UnitMeasure;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUnitMeasureRequest {
    @NotBlank @Size(max = 10)
    private String code;

    @NotBlank @Size(max = 80)
    private String name;

    private boolean allowsDecimals;

    @Min(0) @Max(6)
    private short allowedDecimals;
}