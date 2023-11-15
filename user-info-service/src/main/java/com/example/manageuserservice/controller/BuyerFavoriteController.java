package com.example.manageuserservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.manageuserservice.request.BuyerFavoriteRequest;
import com.example.manageuserservice.response.BuyerFavoriteResponse;
import com.example.manageuserservice.service.buyerfavorite.BuyerFavoriteService;
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
@RequestMapping("api/v1/buyer-favorite")
@Tag(name = "Buyer Favorite")
@CrossOrigin
public class BuyerFavoriteController {

    private final BuyerFavoriteService buyerFavoriteService;

    public BuyerFavoriteController(BuyerFavoriteService buyerFavoriteService) {
        this.buyerFavoriteService = buyerFavoriteService;
    }

    @PostMapping("")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "added shop by user to favorite list")
    public ResponseEntity<ApiResponse<BuyerFavoriteResponse>> addedShopToFavoriteList(@Valid @RequestBody BuyerFavoriteRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "added shop to favorite list successfully",
                buyerFavoriteService.addedShopToFavoriteList(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "remove shop by id from favorite list")
    public ResponseEntity<ApiResponse<?>> removeShopFromFavoriteList(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "remove shop by id from favorite list successfully",
                buyerFavoriteService.removeShopFromFavoriteList(id),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @GetMapping("/current")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "fetched all from favorite's list from current user")
    public ResponseEntity<ApiResponse<List<BuyerFavoriteResponse>>> getAllFromBuyerFavoriteListByOwnerId(){
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched all from favorite's list successfully",
                buyerFavoriteService.getCurrentUserInfo(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "fetched shop from favorite's list by shop id and current id")
    public ResponseEntity<ApiResponse<BuyerFavoriteResponse>> getShopFromFavoriteListByShopIdAndOwnerId(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched shop by id from favorite's list successfully",
                buyerFavoriteService.getShopFromFavoriteList(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }


}
