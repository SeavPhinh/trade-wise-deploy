package com.example.manageuserservice.request;

import com.example.manageuserservice.model.BuyerFavorite;
import com.example.manageuserservice.model.SellerFavorite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerFavoriteRequest {
    private UUID postId;
    public SellerFavorite toEntity(UUID userId){
        return new SellerFavorite(null, userId,this.postId);
    }

}
