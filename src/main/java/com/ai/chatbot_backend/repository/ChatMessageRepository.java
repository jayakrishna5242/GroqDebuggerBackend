package com.ai.chatbot_backend.repository;

import com.ai.chatbot_backend.dto.ChatMessage;
import com.ai.chatbot_backend.dto.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatSessionOrderByTimestampAsc(ChatSession chatSession);

    List<ChatMessage> findByChatSessionIdOrderByTimestampAsc(Long sessionId);

    void deleteByChatSession(ChatSession chatSession);

    void deleteByChatSessionId(Long sessionId);

    long countByChatSessionId(Long sessionId);
}