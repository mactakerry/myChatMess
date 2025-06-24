package com.example.chatServer.model.dto;

import lombok.Data;

@Data
public class ChatDTO {
    private Long id;
    private String name;
    private long userId1;
    private long userId2;
    private boolean isGroupChat;

    public ChatDTO(Long id, String name, boolean isGroupChat, Long userId1, Long userId2) {
        this.id = id;
        this.name = name;
        this.isGroupChat = isGroupChat;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }
}
