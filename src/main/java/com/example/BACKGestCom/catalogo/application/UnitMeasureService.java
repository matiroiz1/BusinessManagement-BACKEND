package com.example.BACKGestCom.catalogo.application;

import com.example.BACKGestCom.catalogo.api.dtos.UnitMeasure.*;
import com.example.BACKGestCom.catalogo.domain.UnitMeasure;
import com.example.BACKGestCom.catalogo.infra.UnitMeasureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UnitMeasureService {

    private final UnitMeasureRepository unitMeasureRepository;

    @Transactional(readOnly = true)
    public List<UnitMeasureResponse> list() {
        return unitMeasureRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UnitMeasureResponse getById(UUID id) {
        var u = unitMeasureRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unit measure not found"));
        return toResponse(u);
    }

    @Transactional
    public UnitMeasureResponse create(CreateUnitMeasureRequest req) {
        if (unitMeasureRepository.existsByCodeIgnoreCase(req.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Unit measure already exists");
        }

        UnitMeasure u = new UnitMeasure();
        u.setCode(req.getCode());
        u.setName(req.getName());
        u.setAllowsDecimals(req.isAllowsDecimals());
        u.setAllowedDecimals(req.getAllowedDecimals());

        // Minimal rule: if decimals not allowed, force allowedDecimals = 0
        if (!u.isAllowsDecimals()) {
            u.setAllowedDecimals((short) 0);
        }

        u = unitMeasureRepository.save(u);
        return toResponse(u);
    }

    private UnitMeasureResponse toResponse(UnitMeasure u) {
        return UnitMeasureResponse.builder()
                .id(u.getId())
                .code(u.getCode())
                .name(u.getName())
                .allowsDecimals(u.isAllowsDecimals())
                .allowedDecimals(u.getAllowedDecimals())
                .build();
    }
}
