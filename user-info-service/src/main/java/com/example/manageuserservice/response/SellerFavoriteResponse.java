package com.example.manageuserservice.response;

import com.example.commonservice.model.Post;
import com.example.commonservice.model.Shop;
import com.example.commonservice.response.PostResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerFavoriteResponse {
    private UUID id;
    private UUID userId;
    private PostResponse post;
}
