package com.example.backgestcom.catalog.api.dtos.UnitMeasure;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class UnitMeasureResponse {
    UUID id;
    String code;
    String name;
    boolean allowsDecimals;
    short allowedDecimals;
}
