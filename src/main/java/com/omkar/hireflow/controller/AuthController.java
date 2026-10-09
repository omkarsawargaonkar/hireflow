package com.omkar.hireflow.controller;

import com.omkar.hireflow.dto.AuthResponse;
import com.omkar.hireflow.dto.LoginRequest;
import com.omkar.hireflow.dto.UserResponse;
import com.omkar.hireflow.service.TokenService;
import com.omkar.hireflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TokenService tokenService;

    public AuthController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        UserResponse user = userService.login(request.getEmail(), request.getPassword());
        String token = tokenService.createToken(user.getId(), user.getRole());
        return new AuthResponse("Login successful", user, token);
    }
}
