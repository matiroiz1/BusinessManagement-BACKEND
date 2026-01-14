package com.example.BACKGestCom.security.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/**
 * System user used for authentication/authorization.
 *
 * Notes:
 * - Store password as a one-way hash (e.g., BCrypt), never plain text.
 * - Keep this entity inside the security module to avoid coupling other modules to it.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "app_user", schema = "core",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_username", columnNames = "username"),
                @UniqueConstraint(name = "uq_user_email", columnNames = "email")
        })
public class User {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "username", nullable = false, length = 60)
    private String username;

    @Column(name = "email", length = 120)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "last_login_at")
    private Instant lastLoginAt;
}
