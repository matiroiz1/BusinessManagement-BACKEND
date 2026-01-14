package com.example.backgestcom.catalog.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "product_status", schema = "core")
public class ProductStatus {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 80)
    private String name;

    @Builder.Default
    @Column(name = "allows_sales", nullable = false)
    private boolean allowsSales = true;

    @Builder.Default
    @Column(name = "allows_purchases", nullable = false)
    private boolean allowsPurchases = true;
}