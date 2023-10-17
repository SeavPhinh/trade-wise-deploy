package com.example.manageuserservice.model;

import com.example.manageuserservice.response.UserInfoResponse;
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
    private LocalDateTime dob;
    private String phoneNumber;
    private String street;
    private String province;
    private String country;
    private String profileImage;
    private UUID userId;

    public UserInfoResponse toDto(){
        return new UserInfoResponse(this.userId,this.gender,this.dob,this.phoneNumber,this.street,this.province,this.country,this.profileImage,this.userId);
    }

}
