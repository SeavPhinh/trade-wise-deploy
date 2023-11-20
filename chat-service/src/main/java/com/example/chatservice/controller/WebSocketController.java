package com.example.chatservice.controller;
import com.example.chatservice.model.ConnectedResponse;
import com.example.chatservice.model.MessageModel;
import com.example.chatservice.service.ChatService;
import com.example.chatservice.service.ChatServiceImpl;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.FileResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/chats")
@CrossOrigin
public class WebSocketController {

    private final ChatService chatService;

    public static final String USER_SERVICE="userService";

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
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userUnderMaintenance")
    public List<MessageModel> getHistory(@PathVariable UUID userId){
        return chatService.getHistoryMessage(userId);
    }

    @GetMapping("/destination/{userId}")
    @SecurityRequirement(name = "oAuth2")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userUnderMaintenance")
    public MessageModel findDestination(@PathVariable UUID userId){
        return chatService.isContainDestination(userId);
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
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userUnderMaintenance")
    public ResponseEntity<ApiResponse<String>> UpdateAllUnseenMessage(@PathVariable UUID userId){
        return new ResponseEntity<>(new ApiResponse<>(
                "read all messages successfully",
                chatService.updateAllMessages(userId),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "upload profile image")
    public ResponseEntity<ApiResponse<FileResponse>> saveFile(@RequestParam(required = false) MultipartFile file,
                                                              HttpServletRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                "image upload to user information successfully",
                chatService.saveFile(file,request),
                HttpStatus.CONTINUE
        ), HttpStatus.CONTINUE);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        Path filePath = Paths.get("chat-service/src/main/resources/storage/", fileName);
        try {
            // Load file as Resource
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
            // Set content type
            String contentType = Files.probeContentType(filePath);
            MediaType mediaType = MediaType.parseMediaType(contentType);
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + fileName);
            headers.setContentType(mediaType);
            // Return ResponseEntity
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(Files.size(filePath))
                    .contentType(mediaType)
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<String> userUnderMaintenance(Exception e) {
        return ResponseEntity.ok("User service is under maintenance.");
    }
}


