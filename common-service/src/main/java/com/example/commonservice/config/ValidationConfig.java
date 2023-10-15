package com.example.commonservice.config;
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
    public static final String EMAIL_REQUIRED_MESSAGE = "Email is required";
    public static final String EMAIL_RESPONSE_MESSAGE = "Email must be a valid email address";
    public static final String ROLE_REQUIRED_MESSAGE = "Roles are required";
    public static final String ROLE_RESPONSE_MESSAGE = "At least one role must be specified";
    public static final int ROLE_VALIDATION_MIN = 1;
    public static final String FIRSTNAME_REQUIRED_MESSAGE = "Firstname cannot be empty";
    public static final String FIRSTNAME_RESPONSE_MESSAGE = "Firstname cannot exceed 50 characters";
    public static final int FIRSTNAME_VALIDATION_MAX = 50;
    public static final String LASTNAME_REQUIRED_MESSAGE = "Lastname cannot be empty";
    public static final String LASTNAME_RESPONSE_MESSAGE = "Lastname cannot exceed 50 characters";
    public static final int LASTNAME_VALIDATION_MAX = 50;
    public static final String OTP_RESPONSE_MESSAGE = "OtpCode must be at least 6 characters";
    public static final int OTP_VALIDATION_MIN = 6;
    public static final String NOTFOUND_USER = "User not found";
    public static final String EMPTY_USER = "Waiting user to registration";
    public static final String WHITE_SPACE = "Password cannot be whitespace";
    public static final String WARNING_ROLE = "Role must include BUYER or SELLER";
    public static final String USER_INVALID = "Email/Username or password is incorrect";
    public static final String INVALID_PASSWORD = "Incorrect password";
    public static final String REQUIRED_OTP = "Sending otpCode is required";
    public static final String INVALID_OTP = "Incorrect otpCode";
    public static final String NOT_MATCHES_PASSWORD = "Password not matched";
    public static final String EXISTING_EMAIL = "This email is already exist";
    public static final String EXISTING_USERNAME = "This username is already exist";
    public static final String PROFILE_IMAGE_RESPONSE = "profile cannot be empty";
    public static final String MESSAGE_NOT_FOUND = "Message not found";


}
