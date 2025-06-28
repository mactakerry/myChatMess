package com.example.chatServer.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {
    private String value;

    public TokenDTO(String value) {
        this.value = value;
    }
}
