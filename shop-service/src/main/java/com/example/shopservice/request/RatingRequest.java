package com.example.shopservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.shopservice.enumeration.Level;
import com.example.shopservice.model.Rating;
import com.example.shopservice.model.Shop;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
