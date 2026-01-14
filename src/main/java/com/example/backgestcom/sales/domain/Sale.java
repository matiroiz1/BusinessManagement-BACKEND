package com.example.backgestcom.sales.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Sale header entity.
 * Stores subtotal/total for fast querying and historical integrity.
 * Customer is not a User; we keep optional customer info for FINAL_CONSUMER MVP.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "sale", schema = "core")
public class Sale {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SaleStatus status = SaleStatus.DRAFT;

    @Column(name = "sold_by_user_id")
    private UUID soldByUserId; // system operator (User.id), not the customer

    // MVP "final consumer" fields (optional). We'll later replace/extend with Customer entity.
    @Column(name = "customer_name", length = 160)
    private String customerName;

    @Column(name = "customer_document", length = 40)
    private String customerDocument;

    @Builder.Default
    @Column(name = "subtotal", nullable = false, precision = 18, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total_discount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total", nullable = false, precision = 18, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "notes")
    private String notes;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    /**
     * Items of the sale.
     * Cascade ALL + orphanRemoval so items are managed by the Sale aggregate.
     */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleItem> items = new ArrayList<>();
}
