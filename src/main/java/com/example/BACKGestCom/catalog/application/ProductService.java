package com.example.BACKGestCom.catalog.application;

import com.example.BACKGestCom.catalog.api.dtos.Product.*;
import com.example.BACKGestCom.catalog.domain.Product;
import com.example.BACKGestCom.catalog.infra.ProductRepository;
import com.example.BACKGestCom.catalog.infra.ProductStatusRepository;
import com.example.BACKGestCom.catalog.infra.ProductTypeRepository;
import com.example.BACKGestCom.catalog.infra.UnitMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final ProductStatusRepository productStatusRepository;
    private final UnitMeasureRepository unitMeasureRepository;

    @Transactional(readOnly = true)
    public List<ProductResponse> listProducts() {
        return productRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        Product p = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toResponse(p);
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest req) {
        if (productRepository.existsBySku(req.getSku())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SKU already exists");
        }

        var type = productTypeRepository.findById(req.getProductTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productTypeId"));

        var status = productStatusRepository.findById(req.getProductStatusId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productStatusId"));

        var um = unitMeasureRepository.findById(req.getUnitMeasureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid unitMeasureId"));

        Product p = new Product();
        p.setSku(req.getSku());
        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setBarcode(req.getBarcode());
        p.setProductType(type);
        p.setProductStatus(status);
        p.setUnitOfMeasure(um);
        p.setCurrentSalePrice(req.getCurrentSalePrice());
        p.setCurrentCost(req.getCurrentCost());
        p.setActive(req.getActive() == null || req.getActive());
        p.setUpdatedAt(Instant.now());

        p = productRepository.save(p);
        return toResponse(p);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest req) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        var type = productTypeRepository.findById(req.getProductTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productTypeId"));

        var status = productStatusRepository.findById(req.getProductStatusId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productStatusId"));

        var um = unitMeasureRepository.findById(req.getUnitMeasureId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid unitMeasureId"));

        p.setName(req.getName());
        p.setDescription(req.getDescription());
        p.setBarcode(req.getBarcode());
        p.setProductType(type);
        p.setProductStatus(status);
        p.setUnitOfMeasure(um);
        p.setCurrentSalePrice(req.getCurrentSalePrice());
        p.setCurrentCost(req.getCurrentCost());
        p.setActive(req.getActive());
        p.setUpdatedAt(Instant.now());

        p = productRepository.save(p);
        return toResponse(p);
    }

    @Transactional
    public void deactivateProduct(UUID id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        p.setActive(false);
        p.setUpdatedAt(Instant.now());
        productRepository.save(p);
    }

    private ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .sku(p.getSku())
                .name(p.getName())
                .description(p.getDescription())
                .barcode(p.getBarcode())
                .active(p.isActive())
                .currentSalePrice(p.getCurrentSalePrice())
                .currentCost(p.getCurrentCost())
                .productTypeId(p.getProductType().getId())
                .productTypeName(p.getProductType().getName())
                .productStatusId(p.getProductStatus().getId())
                .productStatusName(p.getProductStatus().getName())
                .unitMeasureId(p.getUnitOfMeasure().getId())
                .unitMeasureCode(p.getUnitOfMeasure().getCode())
                .build();
    }
}
