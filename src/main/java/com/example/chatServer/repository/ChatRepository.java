package com.example.chatServer.repository;

import com.example.chatServer.model.chat.Chat;
import com.example.chatServer.model.dto.ChatDTO;
import com.example.chatServer.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findByUserId1AndUserId2(long userId1, long userId2);
    Chat findByName(String name);
    List<ChatDTO> findAllByUserId1(long userId1);
    List<ChatDTO> findAllByUserId2(long userId2);

    List<ChatDTO> findGroupChatsByParticipants(User user);
}
