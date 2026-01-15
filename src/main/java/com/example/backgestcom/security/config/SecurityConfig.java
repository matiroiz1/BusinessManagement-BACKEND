package com.example.backgestcom.security.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;


import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtEncoder jwtEncoder() {
        var keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        var secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }

    @Bean
    JwtDecoder jwtDecoder() {
        var keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        var secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String role = jwt.getClaimAsString("role");
            if (role == null || role.isBlank()) return java.util.List.of();
            return java.util.List.of(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return converter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()

                        // Catalog
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/catalog/**")
                        .hasAnyRole("ADMIN","MANAGER","SELLER","WAREHOUSE_OPERATOR")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/catalog/**")
                        .hasAnyRole("ADMIN","MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/catalog/**")
                        .hasAnyRole("ADMIN","MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/catalog/**")
                        .hasAnyRole("ADMIN","MANAGER")

                        // Sales
                        .requestMatchers("/api/sales/**")
                        .hasAnyRole("ADMIN","MANAGER","SELLER")

                        // Inventory
                        .requestMatchers("/api/inventory/**")
                        .hasAnyRole("ADMIN","MANAGER","WAREHOUSE_OPERATOR")

                        // Receiving
                        .requestMatchers("/api/goods-receipts/**")
                        .hasAnyRole("ADMIN","MANAGER","WAREHOUSE_OPERATOR")

                        // Everything else requires JWT
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }


}
