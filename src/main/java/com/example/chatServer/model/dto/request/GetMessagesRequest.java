package com.example.chatServer.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMessagesRequest {
    private long chatId;
}
