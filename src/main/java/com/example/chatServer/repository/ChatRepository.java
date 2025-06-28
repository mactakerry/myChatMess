package com.example.chatServer.repository;

import com.example.chatServer.model.entity.Chat;
import com.example.chatServer.model.dto.ChatDTO;
import com.example.chatServer.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findByName(String name);
    Set<ChatDTO> findChatsByParticipants(User user);

}
