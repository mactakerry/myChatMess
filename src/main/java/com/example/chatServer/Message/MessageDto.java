package com.example.chatServer.Message;

import lombok.Data;

@Data
public class MessageDto {
    private String content;
    private Long chatId;
    private String sender;
}
