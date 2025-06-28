package com.example.chatServer.model.dto;

import lombok.Data;

@Data
public class ChatDTO {
    private Long id;
    private String name;
    private String creator;

    private boolean isGroupChat;

    public ChatDTO(Long id, String name, boolean isGroupChat, String creator) {
        this.id = id;
        this.name = name;
        this.isGroupChat = isGroupChat;
        this.creator = creator;

    }
}
