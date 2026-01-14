package com.example.BACKGestCom.catalog.api.dtos.ProductType;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProductTypeResponse {
    UUID id;
    String name;
    String description;
}
