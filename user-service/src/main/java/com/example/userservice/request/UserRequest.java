package com.example.userservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    @NotBlank(message = ValidationConfig.USER_REQUIRED_MESSAGE)
    @Size(min = ValidationConfig.USER_VALIDATION_MIN,
          max = ValidationConfig.USER_VALIDATION_MAX,
          message = ValidationConfig.USER_RESPONSE_MESSAGE)
    private String username;

    @NotBlank(message = ValidationConfig.PASSWORD_REQUIRED_MESSAGE)

    @Size(min = ValidationConfig.PASSWORD_VALIDATION_MIN,
          message = ValidationConfig.PASSWORD_RESPONSE_MESSAGE,
          max = ValidationConfig.PASSWORD_VALIDATION_MAX)

    @Pattern(regexp = ValidationConfig.PASSWORD_VALIDATION_REG,
            message = ValidationConfig.PASSWORD_RESPONSE_REG_MESSAGE)
    private String password;

    @NotBlank(message = ValidationConfig.EMAIL_REQUIRED_MESSAGE)
    @Email(message = ValidationConfig.EMAIL_RESPONSE_MESSAGE)
    private String email;

    @NotEmpty(message = ValidationConfig.FIRSTNAME_REQUIRED_MESSAGE)
    @Size(max = ValidationConfig.FIRSTNAME_VALIDATION_MAX, message = ValidationConfig.FIRSTNAME_RESPONSE_MESSAGE)
    private String firstname;

    @NotEmpty(message = ValidationConfig.LASTNAME_REQUIRED_MESSAGE)
    @Size(max = ValidationConfig.LASTNAME_VALIDATION_MAX, message = ValidationConfig.LASTNAME_RESPONSE_MESSAGE)
    private String lastname;

}
