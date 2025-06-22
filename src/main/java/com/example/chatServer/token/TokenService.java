package com.example.chatServer.token;

import com.example.chatServer.user.UserRepository;
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

    public boolean validateToken(Token token) {
        if (token == null) {
            return false;
        }

        return tokenRepository.existsById(token.getId());
    }

    public boolean isTimeExpiration(Token token) {
        if (token == null) {
            return true;
        }

        return LocalDate.now().until(token.getDate(), ChronoUnit.DAYS) > 30;
    }
}
