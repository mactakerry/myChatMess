package com.example.chatServer.model.entity;

import com.example.chatServer.model.dto.MessageDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages", indexes = {
        @Index(columnList = "chat_id"),
        @Index(columnList = "sender_id"),
        @Index(columnList = "createdAt")
})
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;


    public Message(String content, Chat chat, User sender) {
        this.content = content;
        this.chat = chat;
        this.sender = sender;
    }

    public Message() {}

    public MessageDTO toDTO() {
        return new MessageDTO(
                content,
                chat.getId(),
                chat.getName(),
                chat.isGroupChat(),
                sender.getUsername(),
                createdAt
        );
    }

}
