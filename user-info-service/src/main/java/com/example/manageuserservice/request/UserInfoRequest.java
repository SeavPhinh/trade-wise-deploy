package com.example.manageuserservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.manageuserservice.model.Gender;
import com.example.manageuserservice.model.UserInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotEmpty(message = ValidationConfig.STREET_REQUIRED_MESSAGE)
    private String street;
    @NotEmpty(message = ValidationConfig.PROVINCE_REQUIRED_MESSAGE)
    private String province;
    @NotEmpty(message = ValidationConfig.COUNTRY_REQUIRED_MESSAGE)
    private String country;
    @NotEmpty(message = ValidationConfig.PROFILE_IMAGE_RESPONSE)
    private String profileImage;

    public UserInfo toEntity(String phoneNumber,UUID createdBy){
        return new UserInfo(null, this.gender,this.dob,phoneNumber,this.street.trim(),this.province.trim(),this.country.trim(),this.profileImage.trim(),createdBy);
    }

}
