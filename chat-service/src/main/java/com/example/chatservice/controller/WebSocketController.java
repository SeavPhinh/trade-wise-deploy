package com.example.chatservice.controller;
import com.example.chatservice.model.ChatMessageEntity;
import com.example.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("api/chat")
public class WebSocketController {

    private final ChatService chatService;

    @Autowired
    public WebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/direct")
    public void sendDirectMessage(@Payload ChatMessageEntity message) {
        chatService.sendDirectMessage(message);
    }

}


