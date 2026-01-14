package com.example.backgestcom.sales.api.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Request to create a DRAFT sale.
 * Totals are computed by the service.
 */
@Data
public class CreateSaleRequest {

    private String customerName;      // optional for FINAL_CONSUMER
    private String customerDocument;  // optional

    private String notes;

    @Valid
    @NotEmpty
    private List<CreateSaleItemRequest> items;
}
