package com.example.chatServer.sevice;

import com.example.chatServer.model.entity.Token;
import com.example.chatServer.repository.TokenRepository;
import com.example.chatServer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    public Token generateToken(long userId) {
        Token token = tokenRepository.findByUserId(userId);
        String value = UUID.randomUUID().toString();
        if (token == null) {
            Token newToken = new Token(value, userId);
            tokenRepository.save(newToken);
            return newToken;
        }

        token.setValue(value);
        token.setDate(LocalDate.now());

        tokenRepository.save(token);

        return token;
    }

    public boolean validateToken(String value) {
        Token token = tokenRepository.findByValue(value);
        if (token == null) {
            return false;
        }

        long daysPassed = ChronoUnit.DAYS.between(token.getDate(), LocalDate.now());


        return daysPassed < 30;
    }

    public Token findByValue(String value) {
        return tokenRepository.findByValue(value);
    }
}
