package com.example.shopservice.request;

import com.example.shopservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {

    private String street;
    private String province;
    private String url;

    public Address toEntity(){
        return new Address(null,this.street,this.province,this.url);
    }

}
