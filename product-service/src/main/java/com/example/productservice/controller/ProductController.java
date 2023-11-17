package com.example.productservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.productservice.exception.NotFoundExceptionClass;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ProductResponse;
import com.example.productservice.service.product.ProductService;
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
@RequestMapping("api/v1/products")
@Tag(name = "Product")
@CrossOrigin
public class ProductController {

    private final ProductService productService;
    public static final String SHOP_SERVICE="shopService";

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("")
    @Operation(summary = "shop adding a product by current user")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductRequest postRequest) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop has added new product successfully",
                productService.addProduct(postRequest),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("")
    @Operation(summary = "fetch all products")
    @SecurityRequirement(name = "oAuth2")
    @CircuitBreaker(name = SHOP_SERVICE, fallbackMethod = "shopUnderMaintenance")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(){
        return new ResponseEntity<>(new ApiResponse<>(
                "products fetched successfully",
                productService.getAllProduct(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "fetch product by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "product fetched by id successfully",
                productService.getProductById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/shop/{id}")
    @Operation(summary = "fetch product by shop id")
    @SecurityRequirement(name = "oAuth2")
    @CircuitBreaker(name = SHOP_SERVICE, fallbackMethod = "shopUnderMaintenance")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProductByShopId(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "product fetched by shop id successfully",
                productService.getAllProductByShopId(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete product by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProductById(@PathVariable UUID id){

        return new ResponseEntity<>(new ApiResponse<>(
                "product delete by id successfully",
                productService.deleteProductById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update products by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductById(@PathVariable UUID id,
                                                                            @Valid @RequestBody ProductRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                " Updated products by id successfully",
                productService.updateProductById(id, request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }


    @PostMapping(value = "/upload/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload multiple file")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<?> saveMultiFile(@PathVariable UUID productId,
            @RequestParam(required = false) List<MultipartFile> files,
                                           HttpServletRequest request) throws IOException {
        if(files != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveListFile(productId,files,request));
        }
        throw new NotFoundExceptionClass("No filename to upload");
    }

    @GetMapping("/image")
    @Operation(summary = "fetched image")
    public ResponseEntity<ByteArrayResource> getFileByFileName(@RequestParam String fileName) throws IOException {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(productService.getImage(fileName));
    }

    public ResponseEntity<String> shopUnderMaintenance(Exception e) {
        return ResponseEntity.ok("Shop service is under maintenance.");
    }
}
