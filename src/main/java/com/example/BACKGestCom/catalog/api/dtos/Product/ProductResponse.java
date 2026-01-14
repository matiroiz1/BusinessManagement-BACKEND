package com.example.BACKGestCom.catalog.api.dtos.Product;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@Builder
public class ProductResponse {
    UUID id;
    String sku;
    String name;
    String description;
    String barcode;

    UUID productTypeId;
    String productTypeName;

    UUID productStatusId;
    String productStatusName;

    UUID unitMeasureId;
    String unitMeasureCode;

    BigDecimal currentSalePrice;
    BigDecimal currentCost;
    boolean active;
}
