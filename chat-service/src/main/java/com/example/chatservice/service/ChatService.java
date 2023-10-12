package com.example.chatservice.service;

import com.example.chatservice.model.MessageModel;
import com.example.chatservice.repository.ChatMessageRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AmqpTemplate rabbitTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient;

    @Autowired
    public ChatService(SimpMessagingTemplate messagingTemplate,
                       AmqpTemplate rabbitTemplate,
                       ChatMessageRepository chatMessageRepository, WebClient.Builder webClient) {
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
    }

    public void sendDirectMessage(MessageModel message){
        messagingTemplate.convertAndSendToUser(message.getSenderId().toString()+"&"+message.getReceiverId().toString(), "/private", message);
        message.setId(UUID.randomUUID());
        message.setTimestamp(LocalDateTime.now());
        chatMessageRepository.persistData(message);
    }

    public List<MessageModel> getHistoryMessage(UUID senderId, UUID receiverId) {
        return chatMessageRepository.findHistory(senderId,receiverId);
    }

    public Boolean isContainDestination(UUID firstUser, UUID secondUser) {
        return false;
    }

}



