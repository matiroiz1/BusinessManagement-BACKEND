package com.example.backgestcom.catalog.infra;

import com.example.backgestcom.catalog.domain.UnitMeasure;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UnitMeasureRepository extends JpaRepository<UnitMeasure, UUID> {
    Optional<UnitMeasure> findByCode(String code);
    boolean existsByCode(String code);

    boolean existsByCodeIgnoreCase(@NotBlank @Size(max = 10) String code);
}