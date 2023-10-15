package com.example.shopservice.request;

import com.example.shopservice.enumeration.Level;
import com.example.shopservice.model.Rating;
import com.example.shopservice.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequest {

    private Level level;
    private UUID shopId;

    public Rating toEntity(UUID userId, Shop shop){
        return new Rating(null,userId,this.level,shop);
    }

}
