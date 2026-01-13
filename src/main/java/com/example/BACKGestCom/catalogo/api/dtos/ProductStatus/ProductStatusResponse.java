package com.example.BACKGestCom.catalogo.api.dtos.ProductStatus;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class ProductStatusResponse {
    UUID id;
    String name;
    boolean allowsSales;
    boolean allowsPurchases;
}