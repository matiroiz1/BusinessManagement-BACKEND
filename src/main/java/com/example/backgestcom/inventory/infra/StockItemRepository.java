package com.example.backgestcom.inventory.infra;

import com.example.backgestcom.inventory.domain.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StockItemRepository extends JpaRepository<StockItem, UUID> {

    Optional<StockItem> findByProduct_IdAndDeposit_Id(UUID productId, UUID depositId);

    // Nueva consulta para obtener items críticos
    @Query("SELECT s FROM StockItem s WHERE s.onHand <= s.criticalThreshold")
    List<StockItem> findCriticalItems();
}