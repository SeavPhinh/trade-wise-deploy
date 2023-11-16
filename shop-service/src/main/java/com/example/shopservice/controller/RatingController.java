package com.example.shopservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.shopservice.request.RatingRequest;
import com.example.shopservice.response.RatingResponse;
import com.example.shopservice.response.ShopResponse;
import com.example.shopservice.service.rating.RatingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
@RequestMapping("api/v1/ratings")
@Tag(name = "Rating")
@SecurityRequirement(name = "oAuth2")
@CrossOrigin
public class RatingController {

    private final RatingService ratingService;

    public static final String USER_SERVICE="userService";

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("")
    @Operation(summary = "ratings shop by user")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userUnderMaintenance")
    public ResponseEntity<ApiResponse<RatingResponse>> ratingShop(@Valid @RequestBody RatingRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                "Rating to shop successfully",
                ratingService.ratingShop(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    public ResponseEntity<String> userUnderMaintenance(Exception e) {
        return ResponseEntity.ok("User service is under maintenance.");
    }

}
