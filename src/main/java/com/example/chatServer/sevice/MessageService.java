package com.example.chatServer.sevice;

import com.example.chatServer.exception.ChatNotFoundException;
import com.example.chatServer.exception.DickheadException;
import com.example.chatServer.model.entity.Chat;
import com.example.chatServer.model.entity.Message;
import com.example.chatServer.model.dto.MessageDTO;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.MessageRepository;
import com.example.chatServer.repository.ChatRepository;
import com.example.chatServer.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    @Transactional(readOnly = true)
    @Cacheable(value = "chatMessages", key = "#chatId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    @CacheEvict(value = "chatMessages", key = "#chatId + '*'")
    public Page<Message> getMessagesByChat(Long chatId, Pageable pageable) {
        return messageRepository.findByChatId(chatId, pageable);
    }

    @SneakyThrows
    public MessageDTO createMessage(Long chatId, long senderId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new DickheadException("Сообщение пустое");
        }

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("Чат не найден"));
        User sender = userRepository.findById(senderId).orElseThrow(() -> new UsernameNotFoundException(senderId + " user не найден"));

        Message message = new Message(content, chat, sender);

        Message saved = messageRepository.save(message);
        return saved.toDTO();
    }
}
