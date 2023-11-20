package com.example.notificationservice.controller;

import com.example.notificationservice.model.Notification;
import com.example.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
@CrossOrigin
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @MessageMapping("/alert-notification")
    public Notification sendDirectMessage(@Payload Notification notification){
        notificationService.sendDirectMessage(notification);
        return notification;
    }

    @GetMapping("/history")
    @Operation(summary = "get all notification based on sub-category")
    @SecurityRequirement(name = "oAuth2")
    public List<Notification> getAllNotificationBasedOnCategory(@RequestParam List<String> subCategory){
        return notificationService.historyNotification(subCategory);
    }



}
