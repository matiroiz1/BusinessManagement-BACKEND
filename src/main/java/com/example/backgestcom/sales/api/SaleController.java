package com.example.backgestcom.sales.api;

import com.example.backgestcom.sales.api.dtos.CreateSaleRequest;
import com.example.backgestcom.sales.api.dtos.SaleResponse;
import com.example.backgestcom.sales.application.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Sales REST endpoints.
 * Controllers are thin: validate input + delegate to service.
 */
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @GetMapping
    public List<SaleResponse> list() {
        return saleService.list();
    }

    @GetMapping("/{id}")
    public SaleResponse getById(@PathVariable UUID id) {
        return saleService.getById(id);
    }

    /**
     * Creates a DRAFT sale. "soldByUserId" should later come from authenticated user (JWT/session).
     * For now you can pass it from client or set a fixed user in the service/controller.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse createDraft(
            @RequestBody @Valid CreateSaleRequest req,
            @RequestParam(required = false) UUID soldByUserId
    ) {
        return saleService.createDraft(req);
    }

    /**
     * Confirms a sale: freezes prices, calculates totals, reduces stock and registers movements.
     */
    @PostMapping("/{id}/confirm")
    public SaleResponse confirm(@PathVariable UUID id) {
        return saleService.confirm(id);
    }
}
