package com.example.chatServer.model.dto;

import com.example.chatServer.model.entity.User;
import lombok.Data;

@Data
public class ChatDTO {
    private long id;
    private String name;
    private String creator;

    private boolean isGroupChat;

    public ChatDTO(Long id, String name, boolean isGroupChat, User creator) {
        this.id = id;
        this.name = name;
        this.isGroupChat = isGroupChat;
        this.creator = creator.getUsername();

    }
}
