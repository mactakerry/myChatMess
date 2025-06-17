package com.example.chatServer.Message;

import com.example.chatServer.chat.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    public void sendMessage(Message message) {
        messageRepository.save(message);
    }
}
