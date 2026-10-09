package com.omkar.hireflow.controller;

import com.omkar.hireflow.dto.UserRequest;
import com.omkar.hireflow.dto.UserResponse;
import com.omkar.hireflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@Valid @RequestBody UserRequest request) {
        return userService.registerUser(request);
    }
}