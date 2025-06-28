package com.example.chatServer.sevice;

import com.example.chatServer.model.entity.Chat;
import com.example.chatServer.model.entity.Message;
import com.example.chatServer.model.dto.MessageDTO;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.MessageRepository;
import com.example.chatServer.repository.ChatRepository;
import com.example.chatServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendMessage(Message message) {
        messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "chatMessages", key = "#chatId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Message> getMessagesByChat(Long chatId, Pageable pageable) {
        return messageRepository.findByChatId(chatId, pageable);
    }

    public MessageDTO createMessage(Long chatId, long senderId, String content) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        User sender = userRepository.findById(senderId).orElseThrow();

        Message message = new Message();
        message.setContent(content);
        message.setChat(chat);
        message.setSender(sender);

        Message saved = messageRepository.save(message);
        return saved.toDTO();
    }
}
