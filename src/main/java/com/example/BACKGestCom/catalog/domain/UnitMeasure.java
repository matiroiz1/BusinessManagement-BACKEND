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
@Table(name = "unit_measure", schema = "core")
public class UnitMeasure {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "code", nullable = false, unique = true, length = 10)
    private String code;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "allows_Decimals", nullable = false)
    private boolean allowsDecimals;

    @Column(name = "allowed_Decimals", nullable = false)
    private short allowedDecimals; // 0..6
}
