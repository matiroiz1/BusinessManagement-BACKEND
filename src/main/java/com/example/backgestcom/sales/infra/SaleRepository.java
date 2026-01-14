package com.example.backgestcom.sales.infra;

import com.example.backgestcom.sales.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Persistence interface for Sale aggregate root.
 */
public interface SaleRepository extends JpaRepository<Sale, UUID> {
}
