package com.example.shopservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.shopservice.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {

    @NotEmpty(message = ValidationConfig.EMPTY_ADDRESS)
    @NotNull(message = ValidationConfig.NULL_ADDRESS)
    private String address;
    @NotEmpty(message = ValidationConfig.EMPTY_URL)
    @NotNull(message = ValidationConfig.NULL_URL)
    private String url;

    public Address toEntity(){
        return new Address(null,this.address.trim(),this.url.trim());
    }

}
