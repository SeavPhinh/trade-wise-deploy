package com.example.chatservice.controller;
import com.example.chatservice.model.ConnectedResponse;
import com.example.chatservice.model.MessageModel;
import com.example.chatservice.service.ChatService;
import com.example.chatservice.service.ChatServiceImpl;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.UserContact;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/chats")
//@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class WebSocketController {

    private final ChatService chatService;

    @Autowired
    public WebSocketController(ChatServiceImpl chatServiceImpl) {
        this.chatService = chatServiceImpl;
    }

    @MessageMapping("/private-message")
    public MessageModel sendDirectMessage(@Payload MessageModel message){
        chatService.sendDirectMessage(message);
        return message;
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "getting chat messages with connected user")
    @SecurityRequirement(name = "oAuth2")
    public List<MessageModel> getHistory(@PathVariable UUID userId){
        return chatService.getHistoryMessage(userId);
    }

    @GetMapping("/destination/{firstUser}/{secondUser}")
    @SecurityRequirement(name = "oAuth2")
    public MessageModel findDestination(@PathVariable UUID firstUser,
                                        @PathVariable UUID secondUser){
        return chatService.isContainDestination(firstUser,secondUser);
    }

    @GetMapping("/contact")
    @Operation(summary = "getting all connected user")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<ConnectedResponse>>> getAllContactUser(){
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched all contact user successfully",
                chatService.getAllContactUser(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    @Operation(summary = "update all unseen message")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<String>> UpdateAllUnseenMessage(@PathVariable UUID userId){
        return new ResponseEntity<>(new ApiResponse<>(
                "read all messages successfully",
                chatService.updateAllMessages(userId),
                HttpStatus.OK
        ), HttpStatus.OK);
    }


}


