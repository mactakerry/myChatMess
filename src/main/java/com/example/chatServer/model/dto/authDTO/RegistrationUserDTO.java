package com.example.chatServer.model.dto.authDTO;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class RegistrationUserDTO {
    @NotBlank
    private String username;

    @Transient
    @NotBlank
    @Size(min=2)
    private String password;
}
