package com.example.backgestcom.inventory.infra;

import com.example.backgestcom.inventory.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence interface for StockItem.
 */
public interface StockItemRepository extends JpaRepository<StockItem, UUID> {
    Optional<StockItem> findByProduct_IdAndDeposit_Id(UUID productId, UUID depositId);
}
