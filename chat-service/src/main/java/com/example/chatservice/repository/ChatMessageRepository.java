package com.example.chatservice.repository;

import com.example.chatservice.model.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {
//    List<ChatMessageEntity> findByType(MessageType type);
}

