package com.example.BACKGestCom.inventory.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * Deposit (warehouse) represents a physical/logical storage location where stock is held.
 * Even if you start with one deposit, keeping it as an entity makes the system scalable.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "deposit", schema = "core")
public class Deposit {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 80, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
