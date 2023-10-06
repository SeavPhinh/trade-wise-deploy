package com.example.userservice.service.Mail;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

@Service
public interface EmailService {
    int verifyCode(String account) throws MessagingException;
    int resetPassword(String account) throws MessagingException;
}
