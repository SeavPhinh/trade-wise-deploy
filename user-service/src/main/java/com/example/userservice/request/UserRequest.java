package com.example.userservice.request;

import com.example.commonservice.configuration.ValidationConfig;
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
          max = ValidationConfig.USER_VALIDATION_MAX)
    @Pattern(regexp = ValidationConfig.PASSWORD_VALIDATION_REG,
            message = ValidationConfig.PASSWORD_RESPONSE_REG_MESSAGE)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @NotEmpty(message = "Roles are required")
    @Valid
    @Size(min = 1, message = "At least one role must be specified")
    private List<Role> roles;

    @NotEmpty(message = "Firstname cannot be empty")
    @Size(max = 50, message = "Firstname cannot exceed 50 characters")
    private String firstname;

    @NotEmpty(message = "Lastname are cannot be empty")
    @Size(max = 50, message = "Lastname cannot exceed 50 characters")
    private String lastname;

}
