package com.example.commonservice.model;

import com.example.commonservice.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<Role> roles;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

}
