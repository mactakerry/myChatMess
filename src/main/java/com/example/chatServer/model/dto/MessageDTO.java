package com.example.chatServer.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private String content;
    private Long chatId;
    private String chatName;
    private boolean isGroupChat;
    private String sender;
    private LocalDateTime createdAt;

    public MessageDTO(String content, Long chatId, String chatName, boolean isGroupChat, String sender, LocalDateTime createdAt) {
        this.content = content;
        this.chatId = chatId;
        this.chatName = chatName;
        this.isGroupChat = isGroupChat;
        this.sender = sender;
        this.createdAt = createdAt;
    }


}
