package com.example.manageuserservice.response;

import com.example.commonservice.model.User;
import com.example.manageuserservice.model.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponse {
    private UUID id;
    private Gender gender;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime dob;
    private String phoneNumber;
    private String profileImage;
    private User user;
}
