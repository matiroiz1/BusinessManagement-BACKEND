package com.example.backgestcom.catalog.infra;

import com.example.backgestcom.catalog.domain.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductStatusRepository extends JpaRepository<ProductStatus, UUID> {
    Optional<ProductStatus> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
