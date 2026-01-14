package com.example.backgestcom.purchase.application;

import com.example.backgestcom.catalog.infra.ProductRepository;
import com.example.backgestcom.inventory.application.InventoryService;
import com.example.backgestcom.purchase.api.dtos.*;
import com.example.backgestcom.purchase.domain.*;
import com.example.backgestcom.purchase.infra.GoodsReceiptRepository;
import com.example.backgestcom.security.application.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoodsReceiptService {

    private final GoodsReceiptRepository goodsReceiptRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final CurrentUser currentUser;

    @Transactional
    public GoodsReceiptResponse createDraft(CreateGoodsReceiptRequest req) {
        UUID receivedByUserId = currentUser.id();

        GoodsReceipt gr = new GoodsReceipt();
        gr.setStatus(GoodsReceiptStatus.DRAFT);
        gr.setReceivedByUserId(receivedByUserId);
        gr.setNotes(req.getNotes());
        gr.setCreatedAt(Instant.now());
        gr.setUpdatedAt(Instant.now());

        for (CreateGoodsReceiptItemRequest itemReq : req.getItems()) {
            var product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productId: " + itemReq.getProductId()));

            GoodsReceiptItem item = new GoodsReceiptItem();
            item.setGoodsReceipt(gr);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());

            gr.getItems().add(item);
        }

        gr = goodsReceiptRepository.save(gr);
        return toResponse(gr);
    }
    //----------------------------------------------------------------------------------
    @Transactional
    public GoodsReceiptResponse confirm(UUID goodsReceiptId) {
        UUID performedByUserId = currentUser.id();

        GoodsReceipt gr = goodsReceiptRepository.findById(goodsReceiptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GoodsReceipt not found"));

        if (gr.getStatus() != GoodsReceiptStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "GoodsReceipt is not in DRAFT status");
        }

        for (GoodsReceiptItem item : gr.getItems()) {
            inventoryService.applyPurchaseIn(
                    item.getProduct().getId(),
                    item.getQuantity(),
                    gr.getId(),
                    performedByUserId
            );
        }

        gr.setStatus(GoodsReceiptStatus.CONFIRMED);
        gr.setConfirmedByUserId(performedByUserId);
        gr.setConfirmedAt(Instant.now());
        gr.setUpdatedAt(Instant.now());

        gr = goodsReceiptRepository.save(gr);
        return toResponse(gr);
    }

    //----------------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public GoodsReceiptResponse getById(UUID goodsReceiptId) {
        GoodsReceipt gr = goodsReceiptRepository.findById(goodsReceiptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GoodsReceipt not found"));
        return toResponse(gr);
    }
    //----------------------------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<GoodsReceiptResponse> list() {
        return goodsReceiptRepository.findAll().stream().map(this::toResponse).toList();
    }
    //----------------------------------------------------------------------------------
    private GoodsReceiptResponse toResponse(GoodsReceipt gr) {
        return GoodsReceiptResponse.builder()
                .id(gr.getId())
                .status(gr.getStatus())
                .receivedByUserId(gr.getReceivedByUserId())
                .notes(gr.getNotes())
                .createdAt(gr.getCreatedAt())
                .confirmedAt(gr.getConfirmedAt())
                .updatedAt(gr.getUpdatedAt())
                .items(gr.getItems().stream().map(i -> GoodsReceiptItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProduct().getId())
                        .quantity(i.getQuantity())
                        .build()
                ).toList())
                .build();
    }
}
