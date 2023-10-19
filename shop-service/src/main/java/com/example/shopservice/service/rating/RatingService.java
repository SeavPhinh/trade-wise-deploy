package com.example.shopservice.service.rating;

import com.example.shopservice.request.RatingRequest;
import com.example.shopservice.response.RatingResponse;
import com.example.shopservice.response.ShopResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface RatingService {

    RatingResponse ratingShop(RatingRequest request);
    List<ShopResponse> getRatedShopByCurrentId();
}
