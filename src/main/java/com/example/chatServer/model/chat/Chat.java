package com.example.chatServer.model.chat;

import com.example.chatServer.model.entity.Message;
import com.example.chatServer.model.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column
    private long creatorId;

    @ManyToMany
    @JoinTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public Chat(User u1, User u2) {
        userId1 = u1.getId();
        userId2 = u2.getId();
        name = u1.getUsername() + "-" + u2.getUsername();
    }

    public Chat(String name, Set<User> participants, long creatorId) {
        this.name = name;
        this.participants = participants;
        this.isGroupChat = true;
        this.createdAt = LocalDateTime.now();
        this.creatorId = creatorId;
    }

    public Chat() {}
}
