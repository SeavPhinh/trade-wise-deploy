package com.example.chatservice.service;

import com.example.chatservice.model.Message;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void sendDirectMessage(Message message) {
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/direct", message);
        rabbitTemplate.convertAndSend("directQueue", message);
    }
}

