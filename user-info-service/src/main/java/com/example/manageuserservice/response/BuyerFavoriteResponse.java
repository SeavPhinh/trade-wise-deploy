package com.example.manageuserservice.response;
import com.example.commonservice.response.ShopResponse;
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
    private ShopResponse shop;
    private String profileImage;
}
