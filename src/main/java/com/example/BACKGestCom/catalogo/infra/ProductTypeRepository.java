package com.example.BACKGestCom.catalogo.infra;

import com.example.BACKGestCom.catalogo.domain.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {

    Optional<ProductType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}