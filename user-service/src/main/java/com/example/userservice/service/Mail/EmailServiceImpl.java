package com.example.userservice.service.Mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;

import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {

    private final TemplateEngine emailTemplateEngine;
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String sender;

    private static final String TEMPLATE = "message";

    public EmailServiceImpl(TemplateEngine emailTemplateEngine, JavaMailSender emailSender) {
        this.emailTemplateEngine = emailTemplateEngine;
        this.emailSender = emailSender;
    }

    @Override
    public int verifyCode(String login) throws MessagingException {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        int otp = generateOTP();
        boolean html = true;

        Context thymeleafContext = new Context(LocaleContextHolder.getLocale());
        thymeleafContext.setVariable("name", login);
        thymeleafContext.setVariable("otp", otp);

        final String emailContent = this.emailTemplateEngine.process(TEMPLATE, thymeleafContext);

        messageHelper.setFrom(sender);
        messageHelper.setTo(login);
        messageHelper.setSubject("Verification Code");
        messageHelper.setText(emailContent, html);
        emailSender.send(mimeMessage);
        return otp;
    }

    @Override
    public int resetPassword(String login) throws MessagingException {

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        int otp = generateOTP();
        boolean html = true;

        Context thymeleafContext = new Context(LocaleContextHolder.getLocale());
        thymeleafContext.setVariable("name", login);
        thymeleafContext.setVariable("otp", otp);

        final String emailContent = this.emailTemplateEngine.process(TEMPLATE, thymeleafContext);

        messageHelper.setFrom(sender);
        messageHelper.setTo(login);
        messageHelper.setSubject("OTP Code");
        messageHelper.setText(emailContent, html);
        emailSender.send(mimeMessage);
        return otp;
    }

    public int generateOTP() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }
}
