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
public class UserUpdate {

    @NotBlank(message = ValidationConfig.EMAIL_REQUIRED_MESSAGE)
    @Email(message = ValidationConfig.EMAIL_RESPONSE_MESSAGE)
    private String email;

    @NotEmpty(message = ValidationConfig.ROLE_REQUIRED_MESSAGE)
    @Valid
    @Size(min = ValidationConfig.ROLE_VALIDATION_MIN, message = ValidationConfig.ROLE_RESPONSE_MESSAGE)
    private List<Role> roles;

}
