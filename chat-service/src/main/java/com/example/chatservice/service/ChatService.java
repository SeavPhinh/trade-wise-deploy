package com.example.chatservice.service;

import com.example.chatservice.config.RabbitMQConfig;
import com.example.chatservice.model.ChatMessageEntity;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AmqpTemplate rabbitTemplate;

    @Autowired
    public ChatService(SimpMessagingTemplate messagingTemplate, AmqpTemplate rabbitTemplate) {
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDirectMessage(ChatMessageEntity message) {
        messagingTemplate.convertAndSendToUser(message.getReceiver().getUsername(), "/queue/direct", message);
        rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_QUEUE, message);
    }
}


