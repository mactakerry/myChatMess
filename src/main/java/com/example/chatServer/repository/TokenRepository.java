package com.example.chatServer.repository;

import com.example.chatServer.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByUserId(long userId);

    boolean existsById(long id);

    Token findByValue(String value);
}
