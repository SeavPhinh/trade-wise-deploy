package com.example.chatservice.controller;
import com.example.chatservice.model.MessageModel;
import com.example.chatservice.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;


@Controller
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class WebSocketController {

    private final ChatService chatService;

    @Autowired
    public WebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/private-message")
    public MessageModel sendDirectMessage(@Payload MessageModel message){
        chatService.sendDirectMessage(message);
        return message;
    }

    @GetMapping("/history/{firstUser}/{secondUser}")
    public List<MessageModel> getHistory(@PathVariable UUID firstUser,
                                         @PathVariable UUID secondUser){
        return chatService.getHistoryMessage(firstUser,secondUser);
    }

    @GetMapping("/destination/{firstUser}/{secondUser}")
    public Boolean findDestination(@PathVariable UUID firstUser,
                                   @PathVariable UUID secondUser){
        return chatService.isContainDestination(firstUser,secondUser);
    }
}


