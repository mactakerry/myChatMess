package com.example.chatServer.user;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String password;


    public UserDTO(String username) {
        this.username = username;
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
