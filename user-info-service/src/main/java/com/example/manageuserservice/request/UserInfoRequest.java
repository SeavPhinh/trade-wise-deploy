package com.example.manageuserservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.manageuserservice.model.Gender;
import com.example.manageuserservice.model.UserInfo;
import jakarta.validation.constraints.NotEmpty;
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
    @NotEmpty(message = ValidationConfig.PROFILE_IMAGE_RESPONSE)
    private String profileImage = null;

    public UserInfo toEntity(String phoneNumber, UUID createdBy){
        return new UserInfo(null, this.gender,this.dob,phoneNumber,this.profileImage.trim(),createdBy);
    }
}
