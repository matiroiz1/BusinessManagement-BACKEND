package com.example.backgestcom.purchase.api;

import com.example.backgestcom.purchase.api.dtos.CreateGoodsReceiptRequest;
import com.example.backgestcom.purchase.api.dtos.GoodsReceiptResponse;
import com.example.backgestcom.purchase.application.GoodsReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goods-receipts")
public class GoodsReceiptController {

    private final GoodsReceiptService goodsReceiptService;

    // MVP: recibido por header (después lo sacás del security context)
    @PostMapping
    public GoodsReceiptResponse createDraft(
            @Valid @RequestBody CreateGoodsReceiptRequest req,
            @RequestHeader("X-User-Id") UUID receivedByUserId
    ) {
        return goodsReceiptService.createDraft(req);
    }

    @PostMapping("/{id}/confirm")
    public GoodsReceiptResponse confirm(@PathVariable("id") UUID id) {
        return goodsReceiptService.confirm(id);
    }

    @GetMapping("/{id}")
    public GoodsReceiptResponse getById(@PathVariable("id") UUID id) {
        return goodsReceiptService.getById(id);
    }

    @GetMapping
    public List<GoodsReceiptResponse> list() {
        return goodsReceiptService.list();
    }
}
