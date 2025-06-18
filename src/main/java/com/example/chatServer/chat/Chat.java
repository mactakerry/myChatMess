package com.example.chatServer.chat;

import com.example.chatServer.Message.Message;
import com.example.chatServer.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
@Data
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private long userId1;

    @Column
    private long userId2;

    private boolean isGroupChat = false;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public Chat(User u1, User u2) {
        userId1 = u1.getId();
        userId2 = u2.getId();
        name = u1.getUsername() + "-" + u2.getUsername();
    }

    public Chat() {}
}
