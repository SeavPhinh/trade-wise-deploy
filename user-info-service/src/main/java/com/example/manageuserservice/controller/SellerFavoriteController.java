package com.example.manageuserservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.manageuserservice.request.BuyerFavoriteRequest;
import com.example.manageuserservice.request.SellerFavoriteRequest;
import com.example.manageuserservice.response.BuyerFavoriteResponse;
import com.example.manageuserservice.response.SellerFavoriteResponse;
import com.example.manageuserservice.response.UserInfoResponse;
import com.example.manageuserservice.service.sellerfavorite.SellerFavoriteService;
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
@RequestMapping("api/v1/seller-favorite")
@Tag(name = "Seller Favorite")
@CrossOrigin
public class SellerFavoriteController {

    private final SellerFavoriteService sellerFavoriteService;

    public SellerFavoriteController(SellerFavoriteService sellerFavoriteService) {
        this.sellerFavoriteService = sellerFavoriteService;
    }

    @PostMapping("")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "added posted from user to favorite list")
    public ResponseEntity<ApiResponse<SellerFavoriteResponse>> addedShopToFavoriteList(@Valid @RequestBody SellerFavoriteRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "added posted to favorite list successfully",
                sellerFavoriteService.addedShopToFavoriteList(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "remove posted by id from favorite list")
    public ResponseEntity<ApiResponse<?>> removePostedFromFavoriteList(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "remove posted by id from favorite list successfully",
                sellerFavoriteService.removePostedFromFavoriteList(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/current")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "fetched all posted from favorite's list from current user")
    public ResponseEntity<ApiResponse<List<SellerFavoriteResponse>>> getAllPostedFromSellerFavoriteListByOwnerId(){
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched all posted from favorite's list successfully",
                sellerFavoriteService.getAllPostedFromSellerFavoriteListByOwnerId(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "fetched posted from favorite's list by post id and current id")
    public ResponseEntity<ApiResponse<SellerFavoriteResponse>> getPostedFromFavoriteListByPostedIdAndOwnerId(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched posted by id from favorite's list successfully",
                sellerFavoriteService.getPostedFromFavoriteList(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

}
