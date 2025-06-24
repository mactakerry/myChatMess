package com.example.chatServer.repository;

import com.example.chatServer.model.entity.Message;
import com.example.chatServer.model.dto.MessageDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<MessageDTO> findByChatIdOrderByTimeAsc(Long chatId);
}
