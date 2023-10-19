package com.example.manageuserservice.request;

import com.example.manageuserservice.model.BuyerFavorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerFavoriteRequest {
    private UUID shopId;
    public BuyerFavorite toEntity(UUID userId){
        return new BuyerFavorite(null, userId,this.shopId);
    }

}
