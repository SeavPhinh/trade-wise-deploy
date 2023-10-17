package com.example.manageuserservice.request;

import com.example.manageuserservice.model.Gender;
import com.example.manageuserservice.model.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRequest {
    private Gender gender;
    private LocalDateTime dob;
    private String phoneNumber;
    private String street;
    private String province;
    private String country;
    private String profileImage;

    public UserInfo toEntity(UUID createdBy){
        return new UserInfo(null, this.gender,this.dob,this.phoneNumber,this.street,this.province,this.country,this.profileImage,createdBy);
    }

}
