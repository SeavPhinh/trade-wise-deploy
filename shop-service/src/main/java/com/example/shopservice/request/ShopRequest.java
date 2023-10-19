package com.example.shopservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.shopservice.model.Address;
import com.example.shopservice.model.Shop;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopRequest {

    @NotEmpty(message = ValidationConfig.EMPTY_SHOP)
    @NotNull(message = ValidationConfig.NULL_SHOP)
    private String name;
    @NotEmpty(message = ValidationConfig.EMPTY_IMAGE)
    @NotNull(message = ValidationConfig.NULL_IMAGE)
    private String profileImage;
    @Valid
    private AddressRequest address;

    public Shop toEntity(Address address, UUID createdBy){
        return new Shop(null,this.name.trim(),this.profileImage.trim(),createdBy, true, address, LocalDateTime.now(),LocalDateTime.now());
    }

}
