package com.example.userservice.service.Mail;

import com.example.userservice.model.UserLogin;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

@Service
public interface EmailService {
//    void sendMailMessage() throws MessagingException;
    int verifyCode(UserLogin login) throws MessagingException;
}
