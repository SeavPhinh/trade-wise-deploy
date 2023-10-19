package com.example.shopservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.shopservice.request.RatingRequest;
import com.example.shopservice.response.RatingResponse;
import com.example.shopservice.response.ShopResponse;
import com.example.shopservice.service.rating.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@Tag(name = "Rating")
@SecurityRequirement(name = "oAuth2")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/ratings")
    @Operation(summary = "ratings shop by user")
    public ResponseEntity<ApiResponse<RatingResponse>> ratingShop(@Valid @RequestBody RatingRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                "Rating to shop successfully",
                ratingService.ratingShop(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("/ratings/shops/current")
    @Operation(summary = "fetch rated shop by owner id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> getRatedShopByCurrentId(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Rated Shop fetched by owner id successfully",
                ratingService.getRatedShopByCurrentId(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

}
