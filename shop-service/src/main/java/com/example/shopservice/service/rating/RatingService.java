package com.example.shopservice.service.rating;

import com.example.shopservice.request.RatingRequest;
import com.example.shopservice.response.RatingResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface RatingService {

    RatingResponse ratingShop(RatingRequest request);

    RatingResponse updateRating(UUID id, RatingRequest request);
}
