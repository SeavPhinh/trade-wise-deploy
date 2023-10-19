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
    public static final String NOTFOUND_USER_DETAIL = "User detail does not exist";
    public static final String EMPTY_USER = "Waiting user to registration";
    public static final String WHITE_SPACE = "Password cannot be whitespace";
    public static final String WARNING_ROLE = "Role must include BUYER or SELLER";
    public static final String USER_INVALID = "Email/Username or password is incorrect";
    public static final String REQUIRED_OTP = "Sending otpCode is required";
    public static final String INVALID_OTP = "Incorrect otpCode";
    public static final String NOT_MATCHES_PASSWORD = "Password not matched";
    public static final String EXISTING_EMAIL = "This email is already exist";
    public static final String EXISTING_USERNAME = "This username is already exist";
    public static final String PROFILE_IMAGE_RESPONSE = "profile cannot be empty";
    public static final String ILLEGAL_USER = "This account has not verify yet";
    public static final String INVALID_FILE = "Only JPEG, PNG, and TIFF images are allowed";
    public static final String FOUND_DETAIL = "This account is already contain detail information";
    public static final String MIN_MAX_PH = "Phone number must be between 9 and 10 characters";
    public static final int MAX_PH = 9;
    public static final int MIN_PH = 8;
    public static final String GENDER_REQUIRED_MESSAGE = "Gender cannot be empty";
    public static final String STREET_REQUIRED_MESSAGE = "Street cannot be empty";
    public static final String PROVINCE_REQUIRED_MESSAGE = "Province cannot be empty";
    public static final String COUNTRY_REQUIRED_MESSAGE = "Country cannot be empty";
    public static final String INVALID_PH = "Phone number is invalid";
    public static final String GENDER_RESPONSE_MESSAGE = "Gender must choose at least 1";
    public static final String ILLEGAL_FILE = "Invalid file extension. Allowed extensions are: .jpg, .jpeg, .png, .tiff";
    public static final String NULL_GENDER = "Gender cannot be null";
    public static final String SHOP_NOTFOUND = "Shop not found";
    public static final String POST_NOTFOUND = "Posted not found";
    public static final String ALREADY_FAV_TO_SHOP = "This shop you have add to favorite list already";
    public static final String EMPTY_FAV_LIST = "Your favorite list is empty";
    public static final String SHOP_NOTFOUND_IN_LIST = "shop is not found in your list";
    public static final String ILLEGAL_TO_FAV = "you can only favorite to shop";


}
