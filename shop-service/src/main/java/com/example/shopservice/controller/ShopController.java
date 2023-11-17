package com.example.shopservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.shopservice.enumeration.Filter;
import com.example.shopservice.request.ShopRequest;
import com.example.shopservice.response.ShopResponse;
import com.example.shopservice.service.shop.ShopService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/shops")
@Tag(name = "Shop")
@CrossOrigin
public class ShopController {

    private final ShopService shopService;

    public static final String USER_SERVICE="userService";

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("")
    @Operation(summary = "set up shop")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ShopResponse>> setUpShop(@Valid @RequestBody ShopRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop has set up successfully",
                shopService.setUpShop(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("")
    @Operation(summary = "fetch all shops")
    @SecurityRequirement(name = "oAuth2")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userUnderMaintenance")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> getAllShop(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shops fetched successfully",
                shopService.getAllShop(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/best")
    @Operation(summary = "fetch three shop based on ratings")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> bestThreeShop(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Best Shops fetched successfully",
                shopService.getShopBasedOnRating(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "fetch shop by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ShopResponse>> getShopById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop fetched by id successfully",
                shopService.getShopById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "fetch shop by owner id")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userUnderMaintenance")
    public ResponseEntity<ApiResponse<ShopResponse>> getShopByOwnerId(@PathVariable UUID userId){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop fetched by owner id successfully",
                shopService.getShopByOwnerId(userId),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/sort")
    @Operation(summary = "fetch shop by sort")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> sortShop(@RequestParam(defaultValue = "NEWEST") Filter filter){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop fetched based on sorting successfully",
                shopService.getShopBasedOnSort(filter),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/filter")
    @Operation(summary = "fetch shop by filter")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> filterShop(@RequestParam String subCategory){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop fetched based on filter successfully",
                shopService.getShopBasedOnFilter(subCategory),
                HttpStatus.OK
        ), HttpStatus.OK);
    }


    @PutMapping("/current")
    @Operation(summary = "update shop by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShopById(@Valid @RequestBody ShopRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                " Updated shop by id successfully",
                shopService.updateShopById(request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/current/action")
    @Operation(summary = "change to shop's shopAction")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ShopResponse>> shopAction(@RequestParam(defaultValue = "false") Boolean isActive){
        return new ResponseEntity<>(new ApiResponse<>(
                " shop has set to inactive successfully",
                shopService.shopAction(isActive),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "upload file")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "userUnderMaintenance")
    public ResponseEntity<ApiResponse<ShopResponse>> saveFile(@RequestParam(required = false) MultipartFile file,
                                                              HttpServletRequest request) throws IOException {
        return new ResponseEntity<>(new ApiResponse<>(
                "image upload to shop successfully",
                shopService.saveFile(file,request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/image")
    @Operation(summary = "fetched image")
    public ResponseEntity<ByteArrayResource> getFileByFileName(@RequestParam String fileName) throws IOException {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(shopService.getImage(fileName));
    }

    public ResponseEntity<String> userUnderMaintenance(Exception e) {
        return ResponseEntity.ok("User service is under maintenance.");
    }
}
