package com.ai.chatbot_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class ChatRequest {
    @NotBlank(message = "Message cannot be empty")
    private String message;

    private Long sessionId;
}