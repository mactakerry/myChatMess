package com.example.chatServer.model.dto.responce;

public record ApiResponse<T>(T data, String errorCode, String errorMessage) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, null, null);
    }

    public static <T> ApiResponse<T> error(String errorCode, String errorMessage) {
        return new ApiResponse<>(null, errorCode, errorMessage);
    }
}
