package com.example.chatServer.sevice;

import com.example.chatServer.model.entity.Message;
import com.example.chatServer.model.dto.MessageDTO;
import com.example.chatServer.repository.MessageRepository;
import com.example.chatServer.repository.ChatRepository;
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
