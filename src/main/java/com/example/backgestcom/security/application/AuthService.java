package com.example.backgestcom.security.application;

import com.example.backgestcom.security.api.dtos.LoginResponse;
import com.example.backgestcom.security.domain.User;
import com.example.backgestcom.security.infra.UserRepository;
import com.example.backgestcom.security.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.ttl-minutes:480}")
    private long ttlMinutes;

    @Transactional
    public LoginResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not active");
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlMinutes * 60);

        // sub = userId (UUID) => más fácil para auditoría interna
        String subject = user.getId().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(subject)
                .claim("username", user.getUsername())
                .claim("role", user.getRole().name())
                .build();

        // HS256
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return LoginResponse.builder()
                .accessToken(token)
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
