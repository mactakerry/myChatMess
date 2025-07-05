package com.example.chatServer.model.dto.authDTO;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class LoginUserDTO {
    @NotBlank
    private String username;

    @Transient
    @NotBlank
    private String password;
}
