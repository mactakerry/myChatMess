package com.example.chatServer.user;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = false)
    private String username;

    @Column(nullable = false)
    private String password;


    public User() {}

}
