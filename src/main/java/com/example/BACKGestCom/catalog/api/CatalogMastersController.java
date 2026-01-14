package com.example.BACKGestCom.catalog.api;

import com.example.BACKGestCom.catalog.api.dtos.ProductType.CreateProductTypeRequest;
import com.example.BACKGestCom.catalog.api.dtos.ProductType.ProductTypeResponse;
import com.example.BACKGestCom.catalog.api.dtos.ProductStatus.CreateProductStatusRequest;
import com.example.BACKGestCom.catalog.api.dtos.ProductStatus.ProductStatusResponse;
import com.example.BACKGestCom.catalog.api.dtos.UnitMeasure.CreateUnitMeasureRequest;
import com.example.BACKGestCom.catalog.api.dtos.UnitMeasure.UnitMeasureResponse;
import com.example.BACKGestCom.catalog.application.ProductTypeService;
import com.example.BACKGestCom.catalog.application.ProductStatusService;
import com.example.BACKGestCom.catalog.application.UnitMeasureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogMastersController {

    private final ProductTypeService productTypeService;
    private final ProductStatusService productStatusService;
    private final UnitMeasureService unitMeasureService;

    @GetMapping("/product-types")
    public List<ProductTypeResponse> listProductTypes() { return productTypeService.list(); }

    @PostMapping("/product-types")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductTypeResponse createProductType(@RequestBody @Valid CreateProductTypeRequest req) {
        return productTypeService.create(req);
    }

    @GetMapping("/product-statuses")
    public List<ProductStatusResponse> listProductStatuses() { return productStatusService.list(); }

    @PostMapping("/product-statuses")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductStatusResponse createProductStatus(@RequestBody @Valid CreateProductStatusRequest req) {
        return productStatusService.create(req);
    }

    @GetMapping("/unit-measures")
    public List<UnitMeasureResponse> listUnitMeasures() { return unitMeasureService.list(); }

    @PostMapping("/unit-measures")
    @ResponseStatus(HttpStatus.CREATED)
    public UnitMeasureResponse createUnitMeasure(@RequestBody @Valid CreateUnitMeasureRequest req) {
        return unitMeasureService.create(req);
    }
}
