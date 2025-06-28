package com.example.chatServer.model.entity;

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
    private String value;

    @Column
    private LocalDate date = LocalDate.now();

    @Column
    private long userId;

    public Token() {}

    public Token(String value, long userId) {
        this.value = value;
        this.userId = userId;
    }
}
