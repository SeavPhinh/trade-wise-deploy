package com.example.shopservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.shopservice.request.RatingRequest;
import com.example.shopservice.response.RatingResponse;
import com.example.shopservice.service.rating.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/ratings/{id}")
    @Operation(summary = "update ratings shop by id")
    public ResponseEntity<ApiResponse<RatingResponse>> updateRating(@PathVariable UUID id,
                                                                    @Valid @RequestBody RatingRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                "Rating updated successfully",
                ratingService.updateRating(id,request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

}
