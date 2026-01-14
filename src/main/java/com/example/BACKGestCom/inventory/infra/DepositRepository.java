package com.example.BACKGestCom.inventory.infra;

import com.example.BACKGestCom.inventory.domain.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence interface for Deposit.
 */
public interface DepositRepository extends JpaRepository<Deposit, UUID> {
    Optional<Deposit> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
