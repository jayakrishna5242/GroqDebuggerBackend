package com.ai.chatbot_backend.controller;

import com.ai.chatbot_backend.dto.LoginRequest;
import com.ai.chatbot_backend.dto.RegisterRequest;
import com.ai.chatbot_backend.dto.UserResponse;
import com.ai.chatbot_backend.dto.User;
import com.ai.chatbot_backend.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {
    "http://localhost:5173",
    "http://localhost:3000",
    "https://aura-ai-chatbot-app.vercel.app"
}, allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            log.info("Login attempt - email: '{}', username: '{}'",
                    loginRequest.getEmail(), loginRequest.getUsername());

            User user = userService.login(loginRequest);

            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("email", user.getEmail());
            session.setMaxInactiveInterval(3600);

            Map<String, Object> response = new HashMap<>();
            Map<String, String> userMap = new HashMap<>();
            userMap.put("id", String.valueOf(user.getId()));
            userMap.put("name", user.getFullName() != null ? user.getFullName() : user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("username", user.getUsername());

            response.put("user", userMap);
            response.put("message", "Login successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> checkAuthStatus(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }
        return ResponseEntity.ok(Map.of("authenticated", true, "userId", userId));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Signup attempt - username: '{}', email: '{}'",
                    registerRequest.getUsername(), registerRequest.getEmail());

            UserResponse newUser = userService.register(registerRequest);

            Map<String, Object> response = new HashMap<>();
            Map<String, String> userMap = new HashMap<>();
            userMap.put("id", String.valueOf(newUser.getId()));
            userMap.put("name", newUser.getFullName() != null ? newUser.getFullName() : newUser.getUsername());
            userMap.put("email", newUser.getEmail());
            userMap.put("username", newUser.getUsername());

            response.put("user", userMap);
            response.put("message", "Signup successful");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Signup error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        try {
            User user = userService.getUserById(userId);
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", user.getFullName() != null ? user.getFullName() : user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("username", user.getUsername());

            return ResponseEntity.ok(Map.of("user", userMap));

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
    }
}
