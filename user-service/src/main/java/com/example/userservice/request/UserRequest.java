package com.example.userservice.request;
import com.example.commonservice.enumeration.Role;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
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

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "A valid password must at least 6 characters, and it must include at least one uppercase letter, one lowercase letter, and one number")
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
