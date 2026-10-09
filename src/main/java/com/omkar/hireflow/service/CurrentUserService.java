package com.omkar.hireflow.service;

import com.omkar.hireflow.entity.Role;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.exception.ForbiddenException;
import com.omkar.hireflow.exception.ResourceNotFoundException;
import com.omkar.hireflow.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public static final String TOKEN_ATTRIBUTE = "hireflow.currentUser";

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public CurrentUserService(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public User requireUser(HttpServletRequest request) {
        TokenService.TokenData token = tokenService.parseToken(
                request.getHeader("Authorization"));

        return userRepository.findById(token.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user no longer exists"));
    }

    public User requireRole(HttpServletRequest request, Role requiredRole) {
        User user = requireUser(request);
        if (user.getRole() != requiredRole) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
        return user;
    }
}
