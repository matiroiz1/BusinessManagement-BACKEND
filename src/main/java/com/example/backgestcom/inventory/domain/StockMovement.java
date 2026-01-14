package com.example.backgestcom.inventory.domain;

import com.example.backgestcom.catalog.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * StockMovement is the inventory "kardex" record: every stock change must generate a movement.
 * This gives you traceability (who/when/why) and is later used for analytics/ML.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_movement", schema = "core")
public class StockMovement {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 40)
    private StockMovementType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deposit_id", nullable = false)
    private Deposit deposit;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "reference_type", length = 30)
    private String referenceType; // e.g. "SALE", "PURCHASE_ORDER", "MANUAL_ADJUSTMENT"

    @Column(name = "reference_id")
    private UUID referenceId; // optional: points to the document that caused the movement

    @Column(name = "reason")
    private String reason;

    @Column(name = "notes")
    private String notes;

    @Column(name = "performed_by_user_id")
    private UUID performedByUserId;

}
