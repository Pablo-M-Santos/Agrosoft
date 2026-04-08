package com.agrosoft.auth.dto;

import com.agrosoft.User.domain.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class AuthResponseDTO {

    private String accessToken;
    private String tokenType;
    private Instant expiresAt;
    private UUID userId;
    private String email;
    private AccessLevel accessLevel;
}
