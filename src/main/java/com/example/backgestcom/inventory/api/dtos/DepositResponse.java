package com.example.backgestcom.inventory.api.dtos;

import lombok.Builder;
import lombok.Value;
import java.util.UUID;

@Value
@Builder
public class DepositResponse {
    UUID id;
    String name;
    String description;
}