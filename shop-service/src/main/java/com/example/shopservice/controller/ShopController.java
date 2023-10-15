package com.example.shopservice.controller;

import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.response.ApiResponse;
import com.example.shopservice.request.ShopRequest;
import com.example.shopservice.response.ShopResponse;
import com.example.shopservice.service.shop.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@Tag(name = "Shop")
@SecurityRequirement(name = "oAuth2")
public class ShopController {

    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }


    @PostMapping("/shops")
    @Operation(summary = "set up shop")
    public ResponseEntity<ApiResponse<ShopResponse>> setUpShop(@Valid @RequestBody ShopRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop has set up successfully",
                shopService.setUpShop(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("/shops")
    @Operation(summary = "fetch all shops")
    public ResponseEntity<ApiResponse<List<ShopResponse>>> getAllShop(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shops fetched successfully",
                shopService.getAllShop(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/shops/{id}")
    @Operation(summary = "fetch shop by id")
    public ResponseEntity<ApiResponse<ShopResponse>> getShopById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop fetched by id successfully",
                shopService.getShopById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/shops/owner/{ownerId}")
    @Operation(summary = "fetch shop by owner id")
    public ResponseEntity<ApiResponse<ShopResponse>> getShopByOwnerId(@PathVariable UUID ownerId){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop fetched by owner id successfully",
                shopService.getShopByOwnerId(ownerId),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/shops/{id}")
    @Operation(summary = "delete shop by id")
    public ResponseEntity<ApiResponse<ShopResponse>> deleteShopById(@PathVariable UUID id){

        return new ResponseEntity<>(new ApiResponse<>(
                "post shop by id successfully",
                shopService.deleteShopById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/shops/{id}")
    @Operation(summary = "update shop by id")
    public ResponseEntity<ApiResponse<ShopResponse>> updateShopById(@PathVariable UUID id,
                                                                    @Valid @RequestBody ShopRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                " Updated shop by id successfully",
                shopService.updateShopById(id, request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload multiple file")
    public ResponseEntity<?> saveMultiFile(@RequestParam(required = false) List<MultipartFile> files,
                                           HttpServletRequest request) throws IOException {
        if(files != null){
            return ResponseEntity.status(200).body(shopService.saveListFile(files,request));
        }
        throw new NotFoundExceptionClass("No filename to upload");
    }

}
