package com.example.manageuserservice.response;

import com.example.commonservice.model.Shop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyerFavoriteResponse {
    private UUID id;
    private UUID userId;
    private Shop shop;
}
