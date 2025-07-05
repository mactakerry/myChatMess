package com.example.chatServer.sevice;

import com.example.chatServer.model.dto.ChatDTO;
import com.example.chatServer.model.entity.Chat;
import com.example.chatServer.model.entity.User;
import com.example.chatServer.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;

    @Transactional
    public Chat createPrivateChat(User user1, User user2) {
        Chat newChat = chatRepository.findByName(user1.getUsername() + "-" + user2.getUsername());

        if (newChat == null) {
            newChat = chatRepository.findByName(user2.getUsername() + "-" + user1.getUsername());
            if (newChat == null) {
                newChat = new Chat(user1, user2, user1);
                chatRepository.save(newChat);
            }

        }

        ChatDTO chatDTO = newChat.toDto();
        messagingTemplate.convertAndSendToUser(
                String.valueOf(user1.getUsername()),
                "/queue/chats/new",
                chatDTO
        );

        messagingTemplate.convertAndSendToUser(
                String.valueOf(user2.getUsername()),
                "/queue/chats/new",
                chatDTO
        );

        return newChat;
    }
}
