package com.agrosoft.User.dto;

import com.agrosoft.User.domain.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserResponseDTO {

    private UUID id;
    private String email;
    private AccessLevel accessLevel;
    private LocalDateTime lastLogin;
    private Boolean active;
}


