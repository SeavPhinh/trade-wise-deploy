package com.example.chatservice.listener;

import com.example.chatservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMessageListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketMessageListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE)
    public void receiveDirectMessage(Message message) {
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/direct", message);
    }
}

