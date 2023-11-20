package com.example.notificationservice.service;

import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.CategorySubCategoryResponse;
import com.example.notificationservice.exception.NotFoundExceptionClass;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {


    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final WebClient.Builder webClient;
    private final Keycloak keycloak;
    @Value("${keycloak.realm}")
    private String realm;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate, NotificationRepository notificationRepository, WebClient.Builder webClient, Keycloak keycloak) {
        this.messagingTemplate = messagingTemplate;
        this.notificationRepository = notificationRepository;
        this.webClient = webClient;
        this.keycloak = keycloak;
    }

    @Override
    public void sendDirectMessage(Notification notification) {
        Notification sendNotification = new Notification();
        List<Notification> notifications = new ArrayList<>();
        for(Notification notify : notificationRepository.getAllBySubCategory(notification.getSubCategory())){
            notifications.add(notify);
            sendNotification = notifications.get(0);
        }
        if(sendNotification != null){
            messagingTemplate.convertAndSend("/topic/" + notification.getSubCategory(), notification);
        }
        assert sendNotification != null;
        sendNotification.setMessage(notification.getMessage());
        sendNotification.setId(UUID.randomUUID());
        sendNotification.setUsername(notification.getUsername());
        sendNotification.setSubCategory(notification.getSubCategory());
        sendNotification.setProfileImage(notification.getProfileImage());
        sendNotification.setCreatedDate(LocalDateTime.now());
        notificationRepository.persistData(sendNotification);
    }

    @Override
    public List<Notification> historyNotification(List<String> subCategory) {
        List<Notification> response = new ArrayList<>();
        for (String category : subCategory) {
            categoriesList(category);
            response.addAll(notificationRepository.getAllBySubCategory(category));
        }
        if (!response.isEmpty()){
            return response;
        }
        throw new NotFoundExceptionClass("Notification is not containing");
    }

    // Returning list category
    public String categoriesList(String categories) {
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        try {
            CategorySubCategoryResponse subName = covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                    .baseUrl("http://8.222.225.41:8087/")
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("api/v1/sub-categories")
                            .queryParam("name", categories.toUpperCase())
                            .build())
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), CategorySubCategoryResponse.class);
            return subName.getSubCategory().getName();
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
    }


}
