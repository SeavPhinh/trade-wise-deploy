package com.example.manageuserservice.model;

import com.example.commonservice.model.User;
import com.example.manageuserservice.response.UserInfoResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime dob;
    private String phoneNumber;
    @Column(nullable = false)
    private String profileImage;
    private UUID userId;

    public UserInfoResponse toDto(User user){
        return new UserInfoResponse(this.userId,this.gender,this.dob,this.phoneNumber,this.profileImage,user);
    }

}
