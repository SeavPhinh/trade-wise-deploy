package com.example.commonservice.response;

import com.example.commonservice.enumeration.Gender;
import com.example.commonservice.model.User;
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
    private String profileImage;
    private User user;
}
