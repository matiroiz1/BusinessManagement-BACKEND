package com.example.backgestcom.purchase.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goods_receipt")
@Getter @Setter

public class GoodsReceipt {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoodsReceiptStatus status;

    @Column(nullable = false)
    private UUID receivedByUserId;

    @Column
    private UUID confirmedByUserId;

    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant confirmedAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "goodsReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GoodsReceiptItem> items = new ArrayList<>();
}
