package com.example.chatservice.repository;

import com.example.chatservice.model.MessageModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<MessageModel, UUID> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO chats (id,content, sender_id, receiver_id, timestamp,status) VALUES (:#{#model.id},CAST(:#{#model.content} AS JSON), :#{#model.senderId}, :#{#model.receiverId}, :#{#model.timestamp}, :#{#model.status})", nativeQuery = true)
    void persistData(@Param("model") MessageModel model);

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM chats WHERE sender_id = :#{#senderId} AND receiver_id = :#{#receiverId} OR receiver_id = :#{#senderId} AND sender_id = :#{#receiverId} ORDER BY timestamp ", nativeQuery = true)
    List<MessageModel> findHistory(UUID senderId, UUID receiverId);

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM chats WHERE sender_id = :#{#userId} OR receiver_id = :#{#userId}", nativeQuery = true)
    List<MessageModel> getUserByCurrentUserId(UUID userId);

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM chats WHERE receiver_id = :#{#userId} AND status = false", nativeQuery = true)
    List<MessageModel> getAllUnseenMessage(UUID userId);

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM chats WHERE sender_id = :#{#id} AND receiver_id = :#{#userId} OR receiver_id = :#{#id} AND sender_id = :#{#userId} ORDER BY timestamp", nativeQuery = true)
    List<MessageModel> getAllMessageWithConnectedUser(UUID id, UUID userId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE chats set status = 'true' WHERE sender_id = :#{#id} AND receiver_id = :#{#userId} OR receiver_id = :#{#id} AND sender_id = :#{#userId}", nativeQuery = true)
    void updateAllUnseenMessages(UUID id, UUID userId);

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM chats WHERE sender_id = :#{#id} OR receiver_id = :#{#id}", nativeQuery = true)
    List<MessageModel> getByUserId(UUID id);
}

