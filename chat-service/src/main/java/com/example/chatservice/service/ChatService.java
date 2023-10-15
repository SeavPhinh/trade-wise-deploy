package com.example.chatservice.service;

import com.example.chatservice.exception.NotFoundExceptionClass;
import com.example.chatservice.model.MessageModel;
import com.example.chatservice.repository.ChatMessageRepository;
import com.example.commonservice.config.ValidationConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final AmqpTemplate rabbitTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient;

    @Autowired
    public ChatService(SimpMessagingTemplate messagingTemplate,
                       AmqpTemplate rabbitTemplate,
                       ChatMessageRepository chatMessageRepository, WebClient.Builder webClient) {
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
    }

    public void sendDirectMessage(MessageModel message){

        MessageModel userDestination = isContainDestination(message.getSenderId(), message.getReceiverId());

        if(userDestination != null){
            messagingTemplate.convertAndSendToUser(userDestination.getSenderId()+"&"+userDestination.getReceiverId(), "/private", message);
        }else {
            messagingTemplate.convertAndSendToUser(message.getSenderId()+"&"+message.getReceiverId(), "/private", message);
        }

        message.setId(UUID.randomUUID());
        message.setTimestamp(LocalDateTime.now());
        chatMessageRepository.persistData(message);

    }

    public List<MessageModel> getHistoryMessage(UUID senderId, UUID receiverId) {
        return chatMessageRepository.findHistory(senderId,receiverId);
    }

    public MessageModel isContainDestination(UUID firstUser, UUID secondUser) {

//        MessageModel message = chatMessageRepository.findFirstMessage(firstUser,secondUser);

        List<MessageModel> messages = new ArrayList<>();
        for (MessageModel message : chatMessageRepository.findAll()) {
            if(message.getReceiverId().toString().equalsIgnoreCase(firstUser.toString()) &&
               message.getSenderId().toString().equalsIgnoreCase(secondUser.toString()) ||
               message.getReceiverId().toString().equalsIgnoreCase(secondUser.toString()) &&
               message.getSenderId().toString().equalsIgnoreCase(firstUser.toString())
            ){
                messages.add(message);
                return messages.get(0);
            }
        }
        return null;
    }
}



