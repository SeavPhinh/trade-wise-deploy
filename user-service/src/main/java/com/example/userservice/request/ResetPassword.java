package com.example.userservice.request;

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
public class ResetPassword {

    @NotEmpty
    @NotBlank
    private String account;
    @NotEmpty
    @NotBlank
    private String otpCode;
    @NotBlank(message = ValidationConfig.PASSWORD_REQUIRED_MESSAGE)
    @Size(min = ValidationConfig.PASSWORD_VALIDATION_MIN, message = ValidationConfig.PASSWORD_RESPONSE_MESSAGE)
    @Pattern(regexp = ValidationConfig.PASSWORD_VALIDATION_REG, message = ValidationConfig.PASSWORD_RESPONSE_REG_MESSAGE)
    private String newPassword;
    @NotBlank(message = ValidationConfig.PASSWORD_REQUIRED_MESSAGE)
    @Size(min = ValidationConfig.PASSWORD_VALIDATION_MIN, message = ValidationConfig.FIRSTNAME_RESPONSE_MESSAGE)
    @Pattern(regexp = ValidationConfig.PASSWORD_VALIDATION_REG, message = ValidationConfig.PASSWORD_RESPONSE_REG_MESSAGE)
    private String confirmPassword;

}
