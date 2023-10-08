package com.example.chatservice.controller;

import com.example.chatservice.config.RabbitMQConfig;
import com.example.chatservice.model.Message;
import com.example.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api/chat")
public class WebSocketController {

    private final ChatService chatService;

    @Autowired
    WebSocketController(ChatService chatService){
        this.chatService = chatService;
    }

    @MessageMapping("/direct")
    @SendTo(RabbitMQConfig.DIRECT_QUEUE)
    public Message sendDirectMessage(Message message) {
        return message;
    }

}

