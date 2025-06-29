package com.example.chatServer.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chats", indexes = @Index(columnList = "isGroupChat"))
@Data
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isGroupChat = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();

    public Chat(User u1, User u2, User creator) {
        name = u1.getUsername() + "-" + u2.getUsername();
        isGroupChat = false;
        participants.add(u1);
        participants.add(u2);
        this.creator = creator;
    }

    public Chat(String name, Set<User> participants, User creator) {
        this.name = name;
        this.participants.add(creator);
        this.participants.addAll(participants);
        this.isGroupChat = true;
        this.creator = creator;
    }

    public Chat() {}
}
