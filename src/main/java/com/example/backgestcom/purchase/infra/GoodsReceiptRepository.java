package com.example.backgestcom.purchase.infra;

import com.example.backgestcom.purchase.domain.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {
}
