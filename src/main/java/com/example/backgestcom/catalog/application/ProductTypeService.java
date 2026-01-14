package com.example.backgestcom.catalog.application;

import com.example.backgestcom.catalog.api.dtos.ProductType.*;
import com.example.backgestcom.catalog.domain.ProductType;
import com.example.backgestcom.catalog.infra.ProductTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductTypeService {

    private final ProductTypeRepository productTypeRepository;

    @Transactional(readOnly = true)
    public List<ProductTypeResponse> list() {
        return productTypeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductTypeResponse getById(UUID id) {
        var t = productTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product type not found"));
        return toResponse(t);
    }

    @Transactional
    public ProductTypeResponse create(CreateProductTypeRequest req) {
        if (productTypeRepository.existsByNameIgnoreCase(req.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product type already exists");
        }
        ProductType t = new ProductType();
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t = productTypeRepository.save(t);
        return toResponse(t);
    }

    private ProductTypeResponse toResponse(ProductType t) {
        return ProductTypeResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .build();
    }
}
