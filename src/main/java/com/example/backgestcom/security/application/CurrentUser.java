package com.example.backgestcom.security.application;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CurrentUser {

    public UUID id() {
        Jwt jwt = jwt();
        return UUID.fromString(jwt.getSubject()); // sub = userId
    }

    public String username() {
        return jwt().getClaimAsString("username");
    }

    public String role() {
        return jwt().getClaimAsString("role");
    }

    private Jwt jwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt)) {
            throw new IllegalStateException("No authenticated JWT found in SecurityContext");
        }
        return (Jwt) auth.getPrincipal();
    }
}
