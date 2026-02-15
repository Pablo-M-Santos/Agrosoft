package com.agrosoft.User.dto;

import com.agrosoft.User.domain.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequestDTO {

    private String email;
    private String password;
    private AccessLevel accessLevel;
}
