package com.example.chatServer.token;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @Column
    private LocalDate date = LocalDate.now();

    @Column
    private long userId;

    public Token() {}

    public Token(String name, long userId) {
        this.name = name;
        this.userId = userId;
    }
}
