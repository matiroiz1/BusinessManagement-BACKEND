package com.example.BACKGestCom.catalogo.api;

import com.example.BACKGestCom.catalogo.api.dtos.Product.*;
import com.example.BACKGestCom.catalogo.api.dtos.ProductStatus.*;
import com.example.BACKGestCom.catalogo.api.dtos.ProductType.*;
import com.example.BACKGestCom.catalogo.api.dtos.UnitMeasure.*;
import com.example.BACKGestCom.catalogo.application.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/catalog/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> list() { return productService.listProducts(); }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id) { return productService.getProductById(id); }

    @GetMapping("/by-sku/{sku}")
    public ProductResponse getBySku(@PathVariable String sku) { return productService.getProductBySku(sku); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody @Valid CreateProductRequest req) {
        return productService.createProduct(req);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateProductRequest req) {
        return productService.updateProduct(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) { productService.deactivateProduct(id); }
}
