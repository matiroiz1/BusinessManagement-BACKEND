package com.example.backgestcom.security.api;

import com.example.backgestcom.security.api.dtos.LoginRequest;
import com.example.backgestcom.security.api.dtos.LoginResponse;
import com.example.backgestcom.security.application.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req.getUsername(), req.getPassword());
    }

    // endpoint de prueba: devuelve los claims del token
    @GetMapping("/me")
    public Jwt me(@org.springframework.security.core.annotation.AuthenticationPrincipal Jwt jwt) {
        return jwt;
    }
}
