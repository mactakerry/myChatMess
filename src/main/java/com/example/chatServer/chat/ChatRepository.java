package com.example.chatServer.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findByUserId1AndUserId2(long userId1, long userId2);
    Chat findByName(String name);
    List<ChatDTO> findAllByUserId1(long userId1);
    List<ChatDTO> findAllByUserId2(long userId2);
}
