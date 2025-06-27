package com.example.chatServer.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() { // Не говорим что именно неверно
        super("Invalid credentials");
    }
}
