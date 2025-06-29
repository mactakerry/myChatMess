package com.example.chatServer.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDTO {
    @NotBlank
    private String username;

    @Size(min=2)
    private String password;
}
