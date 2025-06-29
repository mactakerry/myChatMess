package com.example.chatServer.exception;

public class InvalidTokenException extends Exception {
    public InvalidTokenException(String invalidToken) {
        super(invalidToken);
    }
}
