package com.example.chatservice.listener;

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

//    @RabbitListener(queues = RabbitMQConfig.DIRECT_QUEUE)
//    public void receiveDirectMessage(ChatMessageEntity message) {
//        messagingTemplate.convertAndSendToUser(message.getReceiver().getUsername(), "/queue/direct", message);
//    }
}

