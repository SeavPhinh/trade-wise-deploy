package com.example.notificationservice.service;

import com.example.notificationservice.model.Notification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NotificationService {

    void sendDirectMessage(Notification notification);

    List<Notification> historyNotification(List<String> subCategory);
}
