package com.example.productforsaleservice.controller;

import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.response.ApiResponse;
import com.example.productforsaleservice.request.ProductForSaleRequest;
import com.example.productforsaleservice.response.ProductForSaleResponse;
import com.example.productforsaleservice.service.ProductForSaleService;
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
@Tag(name = "Product For Sales")
@SecurityRequirement(name = "oAuth2")
public class ProductForSaleController {

    private final ProductForSaleService productForSaleService;

    public ProductForSaleController(ProductForSaleService productForSaleService) {
        this.productForSaleService = productForSaleService;
    }

    @PostMapping(value = "/product-for-sales")
    @Operation(summary = "shop adding a product to buyer")
    public ResponseEntity<ApiResponse<ProductForSaleResponse>> addProductToPost(@Valid @RequestBody ProductForSaleRequest postRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop has added a product to buyer's post successfully",
                productForSaleService.addProductToPost(postRequest),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("/product-for-sales")
    @Operation(summary = "fetch all products")
    public ResponseEntity<ApiResponse<List<ProductForSaleResponse>>> getAllProductForSale(){
        return new ResponseEntity<>(new ApiResponse<>(
                "products fetched successfully",
                productForSaleService.getAllProduct(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/product-for-sales/{id}")
    @Operation(summary = "fetch product by id")
    public ResponseEntity<ApiResponse<ProductForSaleResponse>> getProductForSaleById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "product fetched by id successfully",
                productForSaleService.getProductById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/product-for-sales/post/{id}")
    @Operation(summary = "fetch product by posted id")
    public ResponseEntity<ApiResponse<List<ProductForSaleResponse>>> getProductForSaleByPostId(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "product fetched by posted id successfully",
                productForSaleService.getProductByPostId(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/product-for-sales/{id}")
    @Operation(summary = "delete product by id")
    public ResponseEntity<ApiResponse<ProductForSaleResponse>> deleteProductForSaleById(@PathVariable UUID id){

        return new ResponseEntity<>(new ApiResponse<>(
                "product delete by id successfully",
                productForSaleService.deleteProductById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/product-for-sales/{id}")
    @Operation(summary = "update post by id")
    public ResponseEntity<ApiResponse<ProductForSaleResponse>> updateProductForSaleById(@PathVariable UUID id,
                                                                       @Valid @RequestBody ProductForSaleRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                " updated product by id successfully",
                productForSaleService.updateProductById(id, request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload multiple file")
    public ResponseEntity<?> saveMultiFile(@RequestParam(required = false) List<MultipartFile> files,
                                           HttpServletRequest request) throws IOException {
        if(files != null){
            return ResponseEntity.status(200).body(productForSaleService.saveListFile(files,request));
        }
        throw new NotFoundExceptionClass("No filename to upload");
    }

}
