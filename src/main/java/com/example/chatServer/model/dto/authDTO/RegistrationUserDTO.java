package com.example.chatServer.model.dto.authDTO;

import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;


@Getter
public class RegistrationUserDTO {
    @NotBlank
    private String username;

    @Transient
    @NotBlank
    @Size(min=2)
    private String password;
}
