package com.example.chatservice.listener;

import com.example.chatservice.model.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    public WebSocketMessageListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = "directQueue")
    public void receiveDirectMessage(Message message) {
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/direct", message);
    }
}
