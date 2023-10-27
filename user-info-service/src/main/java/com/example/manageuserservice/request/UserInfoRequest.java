package com.example.manageuserservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.manageuserservice.model.Gender;
import com.example.manageuserservice.model.UserInfo;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRequest {

    private Gender gender;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDateTime dob;
    private String phoneNumber;
    @NotEmpty(message = ValidationConfig.PROFILE_IMAGE_RESPONSE)
    private String profileImage;
    @NotEmpty(message = ValidationConfig.FIRSTNAME_REQUIRED_MESSAGE)
    @Size(max = ValidationConfig.FIRSTNAME_VALIDATION_MAX, message = ValidationConfig.FIRSTNAME_RESPONSE_MESSAGE)
    private String firstname;
    @NotEmpty(message = ValidationConfig.LASTNAME_REQUIRED_MESSAGE)
    @Size(max = ValidationConfig.LASTNAME_VALIDATION_MAX, message = ValidationConfig.LASTNAME_RESPONSE_MESSAGE)
    private String lastname;

    public UserInfo toEntity(String phoneNumber,UUID createdBy){
        return new UserInfo(null, this.gender,this.dob,phoneNumber,this.profileImage.trim(),createdBy);
    }

}
