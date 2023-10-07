package com.example.userservice.model;

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
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "A valid password must at least 6 characters, and it must include at least one uppercase letter, one lowercase letter, and one number")
    private String password;
    @NotBlank
    @NotEmpty
    @Size(min = 6, message = "OtpCode must be at least 6 characters")
    private String otpCode;

}
