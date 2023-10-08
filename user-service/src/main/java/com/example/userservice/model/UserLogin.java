package com.example.userservice.model;

import com.example.commonservice.config.ValidationConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
