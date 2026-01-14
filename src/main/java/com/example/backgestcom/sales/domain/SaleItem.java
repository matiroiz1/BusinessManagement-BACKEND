package com.example.backgestcom.sales.domain;

import com.example.backgestcom.catalog.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Sale line item.
 * unitPrice is a snapshot (frozen price) at the time of confirmation.
 * discountAmount allows future discounts without redesigning.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "sale_item", schema = "core")
public class SaleItem {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false, precision = 18, scale = 2)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal unitPrice; // frozen snapshot

    @Builder.Default
    @Column(name = "discount_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "line_subtotal", nullable = false, precision = 18, scale = 2)
    private BigDecimal lineSubtotal = BigDecimal.ZERO; // quantity * unitPrice

    @Builder.Default
    @Column(name = "line_total", nullable = false, precision = 18, scale = 2)
    private BigDecimal lineTotal = BigDecimal.ZERO; // lineSubtotal - discountAmount
}
