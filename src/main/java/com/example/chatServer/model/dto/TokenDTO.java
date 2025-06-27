package com.example.chatServer.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {
    private String name;

    public TokenDTO(String token) {
        name = token;
    }
}
