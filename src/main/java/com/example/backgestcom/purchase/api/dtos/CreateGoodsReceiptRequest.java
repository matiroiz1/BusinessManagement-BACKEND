package com.example.backgestcom.purchase.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateGoodsReceiptRequest {
    private String notes;

    @Valid
    @NotEmpty
    private List<CreateGoodsReceiptItemRequest> items;
}
