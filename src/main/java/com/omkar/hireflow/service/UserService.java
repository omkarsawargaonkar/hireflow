package com.omkar.hireflow.service;

import com.omkar.hireflow.dto.UserRequest;
import com.omkar.hireflow.dto.UserResponse;
import com.omkar.hireflow.entity.User;
import com.omkar.hireflow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.omkar.hireflow.exception.BusinessException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already registered");
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole()
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
        );
    }

    public UserResponse login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid email or password"));

        boolean validPassword;

        try {
            validPassword = passwordEncoder.matches(password, user.getPassword());
        } catch (IllegalArgumentException ex) {
            // Supports users created before password hashing was introduced.
            validPassword = password.equals(user.getPassword());
        }

        if (!validPassword) {
            throw new BusinessException("Invalid email or password");
        }

        // Upgrade an old plain-text password after a successful login.
        if (!user.getPassword().startsWith("$2a$")
                && !user.getPassword().startsWith("$2b$")
                && !user.getPassword().startsWith("$2y$")) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}