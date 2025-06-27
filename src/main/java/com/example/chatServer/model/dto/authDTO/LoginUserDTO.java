package com.example.chatServer.model.dto.authDTO;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
public class LoginUserDTO {
    @NotBlank
    private String username;

    @Transient
    @NotBlank
    private String password;
}
