package com.example.userservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassword {

    private String otpCode;
    private String newPassword;
    private String confirmPassword;

}
