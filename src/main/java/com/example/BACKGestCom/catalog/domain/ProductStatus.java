package com.example.BACKGestCom.catalog.domain;

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

    @Column(name = "allows_sales", nullable = false)
    private boolean allowsSales = true;

    @Column(name = "allows_purchases", nullable = false)
    private boolean allowsPurchases = true;
}