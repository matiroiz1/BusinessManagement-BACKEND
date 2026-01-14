package com.example.backgestcom.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "product", schema = "core")
public class Product {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sku", nullable = false, unique = true, length = 60)
    private String sku;

    @Column(name = "name", nullable = false, length = 160)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "barcode", length = 80)
    private String barcode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_status_id", nullable = false)
    private ProductStatus productStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_measure_id", nullable = false)
    private UnitMeasure unitOfMeasure;

    @Builder.Default
    @Column(name = "current_sale_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentSalePrice = BigDecimal.ZERO;

    @Column(name = "current_cost", precision = 18, scale = 2)
    private BigDecimal currentCost;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt;
}
