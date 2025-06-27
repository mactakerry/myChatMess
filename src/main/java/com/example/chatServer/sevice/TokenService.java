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
        String name = UUID.randomUUID().toString();
        if (token == null) {
            Token newToken = new Token(name, userId);
            tokenRepository.save(newToken);
            return newToken;
        }

        token.setName(name);
        token.setDate(LocalDate.now());

        tokenRepository.save(token);

        return token;
    }

    public boolean validateToken(String tokenName) {
        Token token = tokenRepository.findByName(tokenName);
        if (token == null) {
            return false;
        }

        long daysPassed = ChronoUnit.DAYS.between(token.getDate(), LocalDate.now());


        return daysPassed > 30;
    }

    public Token findByName(String name) {
        return tokenRepository.findByName(name);
    }
}
