package com.example.chatservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.chatservice.exception.NotFoundExceptionClass;
import com.example.chatservice.model.ConnectedResponse;
import com.example.chatservice.model.MessageModel;
import com.example.chatservice.model.MessageResponse;
import com.example.chatservice.repository.ChatMessageRepository;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.UserContact;
import com.example.commonservice.response.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService{

    private final SimpMessagingTemplate messagingTemplate;
    private final AmqpTemplate rabbitTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final WebClient.Builder userWeb;

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    public ChatServiceImpl(SimpMessagingTemplate messagingTemplate,
                           AmqpTemplate rabbitTemplate,
                           ChatMessageRepository chatMessageRepository, WebClient.Builder webClient, WebClient.Builder userWeb) {
        this.messagingTemplate = messagingTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.userWeb = userWeb;
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
        message.setStatus(false);
        chatMessageRepository.persistData(message);

    }

    public List<MessageModel> getHistoryMessage(UUID connectedUser) {
        List<MessageModel> model = chatMessageRepository.findHistory(connectedUser, UUID.fromString(currentUser()));
        if(!model.isEmpty()){
            return model;
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_MESSAGE);
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

    public List<ConnectedResponse> getAllContactUser() {
        checkChat(UUID.fromString(currentUser()));
        List<ConnectedResponse> responses = new ArrayList<>();
        List<UUID> uniqueIds = new ArrayList<>();
        List<MessageModel> listMessage = chatMessageRepository.getUserByCurrentUserId(UUID.fromString(currentUser()));

        if(!listMessage.isEmpty()){

            for (MessageModel message : listMessage) {
                UUID senderId = message.getSenderId();
                UUID receiverId = message.getReceiverId();
                if (senderId != null && senderId.equals(UUID.fromString(currentUser())) && !uniqueIds.contains(receiverId)) {
                    uniqueIds.add(receiverId);
                }
                if (receiverId != null && receiverId.equals(UUID.fromString(currentUser())) && !uniqueIds.contains(senderId)) {
                    uniqueIds.add(senderId);
                }
            }

            for (UUID id : uniqueIds) {
                ConnectedResponse connectedResponse = new ConnectedResponse();
                User user = getUserById(id);
                UserInfoResponse userInfo = getUserInfoById(user.getId());
                if(userInfo == null){
                    connectedResponse.setUser((new UserContact(user.getId(),user.getUsername(),null)));
                }else{
                    connectedResponse.setUser((new UserContact(user.getId(),user.getUsername(),userInfo.getProfileImage())));
                }

                int lastIndex = chatMessageRepository.getAllMessageWithConnectedUser(id, UUID.fromString(currentUser())).size()-1;

                String userType = null;
                if(chatMessageRepository.getAllMessageWithConnectedUser(id, UUID.fromString(currentUser())).get(lastIndex).getSenderId().toString().equals(currentUser())){
                    userType = "Sender";
                }else if((chatMessageRepository.getAllMessageWithConnectedUser(id, UUID.fromString(currentUser())).get(lastIndex).getReceiverId().toString().equals(currentUser()))){
                    userType = "Receiver";
                }
                connectedResponse.setMessage(new MessageResponse(
                        chatMessageRepository.getAllMessageWithConnectedUser(id, UUID.fromString(currentUser())).get(lastIndex),
                        userType,
                        chatMessageRepository.getAllMessageWithConnectedUser(id, UUID.fromString(currentUser())).get(lastIndex).getTimestamp()
                ));

                List<MessageModel> unseenCount = chatMessageRepository.getAllUnseenMessage(UUID.fromString(currentUser()));
                connectedResponse.setUnseenCount(unseenCount.size());
                responses.add(connectedResponse);
            }
            return responses;
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_USER_CONTACT);
    }

    @Override
    public String updateAllMessages(UUID connectedUser) {
        checkChat(connectedUser);
        checkChat(UUID.fromString(currentUser()));
        chatMessageRepository.updateAllUnseenMessages(connectedUser,UUID.fromString(currentUser()));
        return "Messages updated successfully";
    }


    // Validation if user haven't chat with other
    public void checkChat(UUID id){
        if(chatMessageRepository.getByUserId(id) == null){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_YET_TEXTING);
        }
    }

    // Return User
    public User getUserById(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        try{
            return covertSpecificClass.convertValue(Objects.requireNonNull(userWeb
                    .build()
                    .get()
                    .uri("api/v1/users/{id}", id)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), User.class);
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
        }
    }

    // Return User
    public UserInfoResponse getUserInfoById(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        try{
            return covertSpecificClass.convertValue(Objects.requireNonNull(userWeb
                    .build()
                    .get()
                    .uri("api/v1/user-info/{userId}", id)
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), UserInfoResponse.class);
        }catch (Exception e){
            return null;
        }
    }

    // Returning Token
    public String currentUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                // Decode to Get User Id
                DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
                return decodedJWT.getSubject();
            }
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

}



