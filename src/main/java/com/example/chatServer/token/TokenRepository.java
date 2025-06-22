package com.example.chatServer.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByUserId(long userId);

    boolean existsById(long id);

    Token findByName(String tokenName);
}
