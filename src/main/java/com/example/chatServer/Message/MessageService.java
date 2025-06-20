package com.example.chatServer.Message;

import com.example.chatServer.chat.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    public void sendMessage(Message message) {
        messageRepository.save(message);
    }

    public List<MessageDTO> getAllMessByChatId(long id) {
        return messageRepository.findByChatIdOrderByTimeAsc(id);
    }
}
