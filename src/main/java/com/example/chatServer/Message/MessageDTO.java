package com.example.chatServer.Message;

import lombok.Data;

@Data
public class MessageDTO {
    private String content;
    private Long chatId;
    private String sender;

    public MessageDTO(String content, Long chatId, String sender) {
        this.content = content;
        this.chatId = chatId;
        this.sender = sender;
    }
}
