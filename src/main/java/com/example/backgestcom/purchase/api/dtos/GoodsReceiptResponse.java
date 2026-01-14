package com.example.backgestcom.purchase.api.dtos;

import com.example.backgestcom.purchase.domain.GoodsReceiptStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GoodsReceiptResponse {
    private UUID id;
    private GoodsReceiptStatus status;
    private UUID receivedByUserId;
    private String notes;
    private Instant createdAt;
    private Instant confirmedAt;
    private Instant updatedAt;
    private List<GoodsReceiptItemResponse> items;
}
