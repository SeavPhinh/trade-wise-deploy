package com.example.commonservice.configuration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {

    public static final String USER_REQUIRED_MESSAGE = "Username is required";
    public static final String USER_RESPONSE_MESSAGE = "Username must be between 3 and 50 characters";
    public static final int USER_VALIDATION_MIN = 3;
    public static final int USER_VALIDATION_MAX = 50;

    public static final String PASSWORD_REQUIRED_MESSAGE = "Password is required";
    public static final String PASSWORD_RESPONSE_MESSAGE = "Password must be at least 6 characters";
    public static final String PASSWORD_RESPONSE_REG_MESSAGE = "A valid password must at least 6 characters, and it must include at least one uppercase letter, one lowercase letter, and one number";
    public static final String PASSWORD_VALIDATION_REG = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()\\-_=+{};:,<.>])(?!.*\\s).{6,}$";
    public static final int PASSWORD_VALIDATION_MIN = 6;
    public static final int PASSWORD_VALIDATION_MAX = 30;



}
