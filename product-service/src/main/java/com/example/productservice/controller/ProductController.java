package com.example.productservice.controller;

import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.response.ApiResponse;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ProductResponse;
import com.example.productservice.service.ProductService;
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
@RequestMapping("api/v1/products")
@Tag(name = "Product")
@SecurityRequirement(name = "oAuth2")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("")
    @Operation(summary = "shop adding a product")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductRequest postRequest){
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop has added new product successfully",
                productService.addProduct(postRequest),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("")
    @Operation(summary = "fetch all products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts(){
        return new ResponseEntity<>(new ApiResponse<>(
                "products fetched successfully",
                productService.getAllProduct(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "fetch product by id")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "product fetched by id successfully",
                productService.getProductById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/shop/{id}")
    @Operation(summary = "fetch product by shop id")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProductByShopId(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "product fetched by shop id successfully",
                productService.getAllProductByShopId(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete product by id")
    public ResponseEntity<ApiResponse<ProductResponse>> deleteProductById(@PathVariable UUID id){

        return new ResponseEntity<>(new ApiResponse<>(
                "product delete by id successfully",
                productService.deleteProductById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update products by id")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductById(@PathVariable UUID id,
                                                                    @Valid @RequestBody ProductRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                " Updated products by id successfully",
                productService.updateProductById(id, request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload multiple file")
    public ResponseEntity<?> saveMultiFile(@RequestParam(required = false) List<MultipartFile> files,
                                           HttpServletRequest request) throws IOException {
        if(files != null){
            return ResponseEntity.status(200).body(productService.saveListFile(files,request));
        }
        throw new NotFoundExceptionClass("No filename to upload");
    }

}
