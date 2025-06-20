package com.example.chatServer.Message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<MessageDTO> findByChatIdOrderByTimeAsc(Long chatId);
}
