package com.example.notificationservice.service;

import com.example.notificationservice.model.PostNotification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface PostNotificationService {

    void sendDirectMessage(PostNotification postNotification);

    List<PostNotification> getAllCommentNotificationByUserId(UUID userId);

    void readCommentNotification(UUID id, UUID userId);

    Boolean checkCommentId(UUID id);

    Integer countUnreadByUserId(UUID userId);

    PostNotification getCommentNotificationByUserIdAndId(UUID id, UUID userId);

    void deleteCommentNotificationByUserIdAndId(UUID userId, UUID id);

    void deleteAllCommentNotificationsByUserId(UUID userId);
}
