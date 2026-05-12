package com.ai.chatbot_backend.repository;

import com.ai.chatbot_backend.dto.ChatSession;
import com.ai.chatbot_backend.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    List<ChatSession> findByUserOrderByUpdatedAtDesc(User user);

    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<ChatSession> findByUser(User user);

    long countByUser(User user);
}