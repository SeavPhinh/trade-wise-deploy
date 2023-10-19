package com.example.userservice.model;

import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLogin {

    @NotBlank
    @NotEmpty
    private String account;
    @NotBlank(message = ValidationConfig.PASSWORD_REQUIRED_MESSAGE)
    @NotEmpty(message = ValidationConfig.PASSWORD_RESPONSE_MESSAGE)
    private String password;
}
