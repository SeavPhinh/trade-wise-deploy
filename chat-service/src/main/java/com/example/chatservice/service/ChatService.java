package com.example.chatservice.service;

import com.example.chatservice.config.RabbitMQConfig;
import com.example.chatservice.model.MessageModel;
import com.example.chatservice.repository.ChatMessageRepository;
import com.example.commonservice.model.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
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
        this.webClient = webClient.baseUrl("http://localhost:8081").build();
    }

    public void sendDirectMessage(MessageModel message) {

        MessageModel staticObj = new MessageModel(
                null,
                "Hello Tester",
                UUID.fromString("68ccfd40-ab04-41c3-a5d9-a4b645703cd5"),
                UUID.fromString("0ec079d8-8411-4d79-b5ae-bff5a87ad181"),
                LocalDateTime.now()
        );

//        User user = webClient.get()
//                        .uri("api/v1/users/{id}", message.getReceiverId())
//                        .retrieve()
//                        .bodyToMono(User.class)
//                        .block();

        messagingTemplate.convertAndSendToUser(String.valueOf(message.getReceiverId()), "/private", message);
//        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_QUEUE, message);
        System.out.println("Checkout " + message);
        chatMessageRepository.save(message);
    }
}



