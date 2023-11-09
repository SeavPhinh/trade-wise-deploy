package com.example.chatservice.service;

import com.example.chatservice.model.ConnectedResponse;
import com.example.chatservice.model.MessageModel;
import com.example.commonservice.response.FileResponse;
import com.example.commonservice.response.UserContact;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    void sendDirectMessage(MessageModel message);

    List<MessageModel> getHistoryMessage(UUID connectedUser);

    MessageModel isContainDestination(UUID userId);

    List<ConnectedResponse> getAllContactUser();

    String updateAllMessages(UUID connectedUser);

    FileResponse saveFile(MultipartFile file, HttpServletRequest request) throws Exception;
}
