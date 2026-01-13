package com.example.BACKGestCom.catalogo.infra;

import com.example.BACKGestCom.catalogo.domain.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductStatusRepository extends JpaRepository<ProductStatus, UUID> {
    Optional<ProductStatus> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
