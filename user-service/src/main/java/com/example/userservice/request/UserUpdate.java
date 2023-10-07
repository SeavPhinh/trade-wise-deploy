package com.example.userservice.request;
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
