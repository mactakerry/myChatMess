package com.example.chatServer.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class UserDTO {
    @NotBlank
    private String username;

    @Size(min=2)
    private String password;
}
