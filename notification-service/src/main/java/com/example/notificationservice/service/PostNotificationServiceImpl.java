package com.example.notificationservice.service;

import com.example.notificationservice.exception.ForbiddenExceptionClass;
import com.example.notificationservice.exception.NotFoundExceptionClass;
import com.example.notificationservice.model.PostNotification;
import com.example.notificationservice.repository.PostNotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PostNotificationServiceImpl implements PostNotificationService{

    private final PostNotificationRepository postNotificationRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public PostNotificationServiceImpl(PostNotificationRepository postNotificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.postNotificationRepository = postNotificationRepository;
        this.messagingTemplate = messagingTemplate;
    }


    @Override
    public void sendDirectMessage(PostNotification postNotification) {
            PostNotification sendNotification = new PostNotification();
            if(postNotification != null){
                messagingTemplate.convertAndSend("/topic/" + postNotification.getPostId(), postNotification);
            }
        assert postNotification != null;
        sendNotification.setId(UUID.randomUUID());
        sendNotification.setPostId(postNotification.getPostId());
        sendNotification.setUserId(postNotification.getUserId());
        sendNotification.setMessage(postNotification.getMessage());
        sendNotification.setShop(postNotification.getShop());
        sendNotification.setProfileShop(postNotification.getProfileShop());
        sendNotification.setDate(LocalDateTime.now());
        sendNotification.setStatus(false);
        postNotificationRepository.persistData(sendNotification);
    }

    @Override
    public List<PostNotification> getAllCommentNotificationByUserId(UUID userId) {
        Boolean isUserExist = postNotificationRepository.checkUserById(userId);
        List<PostNotification> notifications;
        if(isUserExist){
            notifications= postNotificationRepository.getAllNotificationByPostId(userId);
            if(notifications.size()>0){
                return notifications;
            }else{
                throw new NotFoundExceptionClass("There's no notifications");
            }
        } else{
            throw new NotFoundExceptionClass("User not found");
        }
    }

    @Override
    public void readCommentNotification(UUID id, UUID userId) {
        Boolean isCommentExist = checkCommentId(id);
        if(isCommentExist){
            Boolean isUserExist = postNotificationRepository.checkUserById(userId);
            if(isUserExist){
                PostNotification notification = postNotificationRepository.getNotificationByUserIdAndId(id,userId);
                if(notification != null)
                    postNotificationRepository.readCommentNotification(id,userId);
                else
                    throw new ForbiddenExceptionClass("You're not owner of this notification");
            }else{
                throw new NotFoundExceptionClass("User not found");
            }
        }else{
            throw new NotFoundExceptionClass("Comment not found");
        }

    }

    @Override
    public Boolean checkCommentId(UUID id) {
        return postNotificationRepository.checkCommentId(id);
    }

    @Override
    public Integer countUnreadByUserId(UUID userId) {
        Boolean isUserExist = postNotificationRepository.checkUserById(userId);
        if(isUserExist)
            return postNotificationRepository.countUnreadComment(userId);
        else
            throw new NotFoundExceptionClass("User not found");
    }

    @Override
    public PostNotification getCommentNotificationByUserIdAndId(UUID id, UUID userId) {
        Boolean isCommentExist = postNotificationRepository.checkCommentId(id);
        if(isCommentExist){
            Boolean isUserExist = postNotificationRepository.checkUserById(userId);
            if(isUserExist){
                PostNotification notification = postNotificationRepository.getNotificationByUserIdAndId(id,userId);
                if(notification != null)
                    return notification;
                else
                    throw new ForbiddenExceptionClass("You're not owner of this notification");
            }else{
                throw new NotFoundExceptionClass("User not found");
            }
        }else{
            throw new NotFoundExceptionClass("Comment id not found");
        }
    }

    @Override
    public void deleteCommentNotificationByUserIdAndId(UUID userId, UUID id) {
        Boolean isUserExist = postNotificationRepository.checkUserById(userId);
        if(isUserExist){
            Boolean isCommentNotificationExist = checkCommentId(id);
            if(isCommentNotificationExist){
                PostNotification notification = postNotificationRepository.getNotificationByUserIdAndId(id,userId);
                if(notification != null)
                    postNotificationRepository.deleteCommentNotificationByUserIdAndId(id,userId);
                else
                    throw new ForbiddenExceptionClass("You're not the owner of this notification");
            }else{
                throw new NotFoundExceptionClass("Comment not found");
            }
        }else{
            throw new NotFoundExceptionClass("User not found");
        }
    }

    @Override
    public void deleteAllCommentNotificationsByUserId(UUID userId) {
        Boolean isUserExist = postNotificationRepository.checkUserById(userId);
        if(isUserExist)
            postNotificationRepository.deleteNotificationByUserId(userId);
        else
            throw new NotFoundExceptionClass("User not found");
    }
}
