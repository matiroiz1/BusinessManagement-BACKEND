package com.example.backgestcom.inventory.infra;

import com.example.backgestcom.inventory.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Persistence interface for StockMovement (kardex).
 */
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {
    List<StockMovement> findTop200ByProduct_IdAndDeposit_IdOrderByCreatedAtDesc(UUID productId, UUID depositId);
}
