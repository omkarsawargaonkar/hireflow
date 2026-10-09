package com.omkar.hireflow.dto;

public class AuthResponse {

    private String message;
    private UserResponse user;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String message, UserResponse user, String token) {
        this.message = message;
        this.user = user;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public UserResponse getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }
}
