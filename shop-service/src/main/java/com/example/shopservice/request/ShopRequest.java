package com.example.shopservice.request;

import com.example.shopservice.model.Address;
import com.example.shopservice.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopRequest {

    private String name;
    private String profileImage;
    private AddressRequest address;

    public Shop toEntity(Address address, UUID createdBy){
        return new Shop(null,this.name,this.profileImage,createdBy, address, LocalDateTime.now(),LocalDateTime.now());
    }

}
