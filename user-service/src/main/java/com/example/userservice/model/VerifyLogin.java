package com.example.userservice.model;

import com.example.commonservice.config.ValidationConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyLogin {

    @NotBlank
    @NotEmpty
    private String account;
    @NotBlank(message = ValidationConfig.PASSWORD_REQUIRED_MESSAGE)
    @Size(min = ValidationConfig.PASSWORD_VALIDATION_MIN, message = ValidationConfig.PASSWORD_RESPONSE_MESSAGE)
    @Pattern(regexp = ValidationConfig.PASSWORD_VALIDATION_REG, message = ValidationConfig.PASSWORD_RESPONSE_REG_MESSAGE)
    private String password;
    @NotBlank
    @NotEmpty
    @Size(min = ValidationConfig.OTP_VALIDATION_MIN, message = ValidationConfig.OTP_RESPONSE_MESSAGE)
    private String otpCode;

}
