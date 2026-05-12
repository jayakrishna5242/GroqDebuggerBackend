package com.ai.chatbot_backend.service;

import com.ai.chatbot_backend.dto.LoginRequest;
import com.ai.chatbot_backend.dto.RegisterRequest;
import com.ai.chatbot_backend.dto.UserResponse;
import com.ai.chatbot_backend.dto.User;
import com.ai.chatbot_backend.exception.AIServiceException;
import com.ai.chatbot_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse register(RegisterRequest request) {
        log.info("=== REGISTER ATTEMPT === Username: '{}', Email: '{}'",
                request.getUsername(), request.getEmail());

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new AIServiceException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new AIServiceException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AIServiceException("Password is required");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AIServiceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AIServiceException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .build();

        user = userRepository.save(user);
        log.info("=== REGISTER SUCCESS === User saved with ID: {}", user.getId());

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public User login(LoginRequest request) {
        log.info("=== LOGIN ATTEMPT ===");

        // ✅ Support both email and username fields
        String identifier = request.getEmail() != null ? request.getEmail() : request.getUsername();

        if (identifier == null || identifier.isBlank()) {
            throw new AIServiceException("Email or username is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AIServiceException("Password is required");
        }

        log.info("Identifier: '{}'", identifier);

        // Try email first, then username
        User user = userRepository.findByEmail(identifier).orElse(null);
        if (user == null) {
            user = userRepository.findByUsername(identifier).orElse(null);
        }

        if (user == null) {
            log.error("User not found with: {}", identifier);
            throw new AIServiceException("Invalid credentials");
        }

        if (!user.getPassword().equals(request.getPassword())) {
            log.error("Password mismatch for user: {}", user.getUsername());
            throw new AIServiceException("Invalid credentials");
        }

        log.info("=== LOGIN SUCCESS === User: {}", user.getUsername());
        return user;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AIServiceException("User not found"));
    }
}
