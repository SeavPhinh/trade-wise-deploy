package com.example.categoryservice.controller;

import com.example.categoryservice.request.CategoryRequest;
import com.example.categoryservice.response.CategoryResponse;
import com.example.categoryservice.service.CategoryService;
import com.example.commonservice.response.ApiResponse;
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
@RequestMapping("api/v1")
@Tag(name = "Category")
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    @Operation(summary = "fetch all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories fetched successfully",
                categoryService.getAllCategories(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/categories/{id}")
    @Operation(summary = "fetch categories by id")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories fetched by id successfully",
                categoryService.getCategoryById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/categories")
    @Operation(summary = "adding category")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@Valid @RequestBody CategoryRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories added successfully",
                categoryService.addCategory(request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "delete categories by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategoryById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories delete by id successfully",
                categoryService.deleteCategoryById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/categories/{id}")
    @Operation(summary = "update categories by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategoryById(@PathVariable UUID id,
                                                                            @Valid @RequestBody CategoryRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories updated by id successfully",
                categoryService.updateCategoryById(id,request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }


}
