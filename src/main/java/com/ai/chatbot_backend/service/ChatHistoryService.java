package com.ai.chatbot_backend.service;


import com.ai.chatbot_backend.dto.ChatMessage;
import com.ai.chatbot_backend.dto.ChatSession;
import com.ai.chatbot_backend.dto.User;
import com.ai.chatbot_backend.exception.AIServiceException;
import com.ai.chatbot_backend.repository.ChatMessageRepository;
import com.ai.chatbot_backend.repository.ChatSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryService {
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatSession createNewSession(User user, String sessionName) {
        ChatSession session = ChatSession.builder()
                .sessionName(sessionName)
                .user(user)
                .build();

        return chatSessionRepository.save(session);
    }

    @Transactional
    public void saveMessage(Long sessionId, String role, String content) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        ChatMessage message = ChatMessage.builder()
                .role(role)
                .content(content)
                .chatSession(session)
                .build();

        chatMessageRepository.save(message);

        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
    }

    public List<ChatSession> getUserSessions(User user) {
        return chatSessionRepository.findByUserOrderByUpdatedAtDesc(user);
    }

    public List<ChatMessage> getSessionMessages(Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return chatMessageRepository.findByChatSessionOrderByTimestampAsc(session);
    }


    @Transactional
    public ChatSession renameSession(Long sessionId, String newName) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new AIServiceException("Session not found with id: " + sessionId));

        session.setSessionName(newName);
        session.setUpdatedAt(LocalDateTime.now());

        return chatSessionRepository.save(session);
    }

    @Transactional
    public void deleteSession(Long sessionId) {
        chatSessionRepository.deleteById(sessionId);
    }
}