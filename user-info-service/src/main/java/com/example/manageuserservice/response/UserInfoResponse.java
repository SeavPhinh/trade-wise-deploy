package com.example.manageuserservice.response;

import com.example.manageuserservice.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private UUID id;
    private Gender gender;
    private LocalDateTime dob;
    private String phoneNumber;
    private String street;
    private String province;
    private String country;
    private String profileImage;
    private UUID userId;
}
