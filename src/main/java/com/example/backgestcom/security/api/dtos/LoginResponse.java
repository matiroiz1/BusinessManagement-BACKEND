package com.example.backgestcom.security.api.dtos;

import com.example.backgestcom.security.domain.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private UUID userId;
    private String username;
    private UserRole role;
}
