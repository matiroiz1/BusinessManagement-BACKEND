package com.example.BACKGestCom.inventory.domain;

import com.example.BACKGestCom.catalog.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * StockItem represents the current stock level for a given Product in a given Deposit.
 * This is your "on hand" stock used to validate sales and update inventory.
 */
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@Builder
@Entity
@Table(name = "stock_item", schema = "core",
        uniqueConstraints = @UniqueConstraint(name = "uq_stock_product_deposit", columnNames = {"product_id", "deposit_id"}))
public class StockItem {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deposit_id", nullable = false)
    private Deposit deposit;

    @Column(name = "on_hand", nullable = false, precision = 18, scale = 2)
    private BigDecimal onHand = BigDecimal.ZERO;

    @Column(name = "critical_threshold", nullable = false, precision = 18, scale = 2)
    private BigDecimal criticalThreshold = BigDecimal.ZERO;

    @Column(name = "minimum_threshold", precision = 18, scale = 2)
    private BigDecimal minimumThreshold;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
