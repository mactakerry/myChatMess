package com.example.chatServer.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByParticipantsId(Long userId);
    Chat findByName(String name);
}
