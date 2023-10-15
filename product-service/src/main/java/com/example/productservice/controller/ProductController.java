package com.example.productservice.controller;

import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.response.ApiResponse;
import com.example.productservice.model.Product;
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
@RequestMapping("api/v1")
@Tag(name = "Product")
@SecurityRequirement(name = "oAuth2")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/products")
    @Operation(summary = "shop adding a product")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(@Valid @RequestBody ProductRequest postRequest) throws IOException {
        return new ResponseEntity<>(new ApiResponse<>(
                "Shop has added new product successfully",
                productService.addProduct(postRequest),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

//    @GetMapping("/posts")
//    @Operation(summary = "fetch all posts")
//    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPost(){
//        return new ResponseEntity<>(new ApiResponse<>(
//                "Posts fetched successfully",
//                postService.getAllPost(),
//                HttpStatus.OK
//        ), HttpStatus.OK);
//    }
//
//    @GetMapping("/posts/{id}")
//    @Operation(summary = "fetch post by id")
//    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable UUID id){
//        return new ResponseEntity<>(new ApiResponse<>(
//                "User fetched by id successfully",
//                postService.getPostById(id),
//                HttpStatus.OK
//        ), HttpStatus.OK);
//    }
//
//    @DeleteMapping("/posts/{id}")
//    @Operation(summary = "delete post by id")
//    public ResponseEntity<ApiResponse<PostResponse>> deletePostById(@PathVariable UUID id){
//
//        return new ResponseEntity<>(new ApiResponse<>(
//                "post delete by id successfully",
//                postService.deletePostById(id),
//                HttpStatus.OK
//        ), HttpStatus.OK);
//    }
//
//    @PutMapping("/posts/{id}")
//    @Operation(summary = "update post by id")
//    public ResponseEntity<ApiResponse<PostResponse>> updatePostById(@PathVariable UUID id,
//                                                                    @Valid @RequestBody PostRequest request){
//        return new ResponseEntity<>(new ApiResponse<>(
//                " Updated post by id successfully",
//                postService.updatePostById(id, request),
//                HttpStatus.ACCEPTED
//        ), HttpStatus.ACCEPTED);
//    }


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
