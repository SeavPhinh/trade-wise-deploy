package com.example.notificationservice.controller;


import com.example.commonservice.response.ApiResponse;
import com.example.notificationservice.model.PostNotification;
import com.example.notificationservice.service.PostNotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/post-notification")
@CrossOrigin
public class PostNotificationController {
    private final PostNotificationServiceImpl postNotificationService;

    public PostNotificationController(PostNotificationServiceImpl postNotificationService) {
        this.postNotificationService = postNotificationService;
    }

    @MessageMapping("/alert-post-notification")
    public PostNotification sendDirectMessage(@Payload PostNotification notification){
        postNotificationService.sendDirectMessage(notification);
        return notification;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get all comment notifications by user id")
    public ResponseEntity<ApiResponse<List<PostNotification>>> getAllCommentNotificationsByUserId(@PathVariable("userId")UUID userId){
        return new ResponseEntity<>(new ApiResponse<>(
                "You have successfully fetched comment notification by user id",
                postNotificationService.getAllCommentNotificationByUserId(userId),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/{id}/{userId}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "Read notification by user id and id")
    public ResponseEntity<ApiResponse<Void>> readNotificationByUserIdAndId(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId){
        postNotificationService.readCommentNotification(id,userId);
        return new ResponseEntity<>(new ApiResponse<>(
                "You have successfully read comment notification by user id and id",
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/unread/{userId}")
    @Operation(summary = "Get unread notification by user id")
    public ResponseEntity<ApiResponse<Integer>> totalUnreadNotifications(@PathVariable("userId") UUID userId){
        return new ResponseEntity<>(new ApiResponse<>(
                "You have successfully count unread notification by user id",
                postNotificationService.countUnreadByUserId(userId),
                HttpStatus.OK
        ), HttpStatus.OK);
    }


    @GetMapping("/{id}/{userId}")
    @Operation(summary = "Get notification by id and user id")
    public ResponseEntity<ApiResponse<PostNotification>> getNotificationByIdAndUser(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId){
        return new ResponseEntity<>(new ApiResponse<>(
                "You have successfully get notification by id and user id",
                postNotificationService.getCommentNotificationByUserIdAndId(id,userId),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/{userId}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "Delete notification by id and user id")
    public ResponseEntity<ApiResponse<Void>> deleteNotificationByIdAndUserId(@PathVariable("id") UUID id, @PathVariable("userId") UUID userId){
        postNotificationService.deleteCommentNotificationByUserIdAndId(userId,id);
        return new ResponseEntity<>(new ApiResponse<>(
                "You have successfully deleted notification by id and user id",
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "Delete notifications by user id")
    public ResponseEntity<ApiResponse<Void>> deleteNotificationsByUserId(@PathVariable("userId") UUID userId){
        postNotificationService.deleteAllCommentNotificationsByUserId(userId);
        return new ResponseEntity<>(new ApiResponse<>(
                "You have successfully deleted notifications by user id",
                null,
                HttpStatus.OK
        ), HttpStatus.OK);
    }

}
