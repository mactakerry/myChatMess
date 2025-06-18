package com.example.chatServer.chat;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findByUserId1AndUserId2(long userId1, long userId2);
    Chat findByName(String name);
}
