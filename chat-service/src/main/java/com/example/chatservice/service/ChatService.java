package com.example.chatservice.service;

import com.example.chatservice.model.ConnectedResponse;
import com.example.chatservice.model.MessageModel;
import com.example.commonservice.response.UserContact;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    void sendDirectMessage(MessageModel message);

    List<MessageModel> getHistoryMessage(UUID connectedUser);

    MessageModel isContainDestination(UUID firstUser, UUID secondUser);

    List<ConnectedResponse> getAllContactUser();

    String updateAllMessages(UUID connectedUser);
}
