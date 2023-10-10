package com.example.chatservice.controller;
import com.example.chatservice.model.MessageModel;
import com.example.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.UUID;


@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class WebSocketController {

    private final ChatService chatService;

    @Autowired
    public WebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/private-message")
    public MessageModel sendDirectMessage(@Payload MessageModel message) {
        chatService.sendDirectMessage(message);
        return message;
    }

}


