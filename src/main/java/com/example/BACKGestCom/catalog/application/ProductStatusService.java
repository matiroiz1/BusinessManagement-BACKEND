package com.example.BACKGestCom.catalog.application;

import com.example.BACKGestCom.catalog.api.dtos.ProductStatus.*;
import com.example.BACKGestCom.catalog.domain.ProductStatus;
import com.example.BACKGestCom.catalog.infra.ProductStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductStatusService {

    private final ProductStatusRepository productStatusRepository;

    @Transactional(readOnly = true)
    public List<ProductStatusResponse> list() {
        return productStatusRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductStatusResponse getById(UUID id) {
        var s = productStatusRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product status not found"));
        return toResponse(s);
    }

    @Transactional
    public ProductStatusResponse create(CreateProductStatusRequest req) {
        if (productStatusRepository.existsByNameIgnoreCase(req.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product status already exists");
        }
        ProductStatus s = new ProductStatus();
        s.setName(req.getName());
        s.setAllowsSales(req.isAllowsSales());
        s.setAllowsPurchases(req.isAllowsPurchases());
        s = productStatusRepository.save(s);
        return toResponse(s);
    }

    private ProductStatusResponse toResponse(ProductStatus s) {
        return ProductStatusResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .allowsSales(s.isAllowsSales())
                .allowsPurchases(s.isAllowsPurchases())
                .build();
    }
}
