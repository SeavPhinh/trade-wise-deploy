package com.example.categoryservice.controller;

import com.example.categoryservice.request.CategoryRequest;
import com.example.categoryservice.response.CategoryResponse;
import com.example.categoryservice.response.CategorySubCategory;
import com.example.categoryservice.service.category.CategoryService;
import com.example.commonservice.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@Tag(name = "Category")
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    @Operation(summary = "*fetch all categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories fetched successfully",
                categoryService.getAllCategories(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/name")
    @Operation(summary = "*fetch categories by name")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryByName(@RequestParam String name){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories fetched by name successfully",
                categoryService.getCategoryById(name),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/sub-categories")
    @Operation(summary = "*fetch category with sub categories by name")
    public ResponseEntity<ApiResponse<CategorySubCategory>> getCategoryAndSubCategoryByName(@RequestParam String name){
        return new ResponseEntity<>(new ApiResponse<>(
                "Category and SubCategory fetched by name successfully",
                categoryService.getCategoryAndSubCategoryByName(name),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "*adding category")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategoryResponse>> addCategory(@Valid @RequestBody CategoryRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories added successfully",
                categoryService.addCategory(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping("")
    @Operation(summary = "*delete categories by name")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategoryResponse>> deleteCategoryByName(@RequestParam String name){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories delete by name successfully",
                categoryService.deleteCategoryByName(name),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @PutMapping("")
    @Operation(summary = "*update categories by name")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategoryById(@RequestParam String name,
                                                                            @Valid @RequestBody CategoryRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "Categories updated by name successfully",
                categoryService.updateCategoryByName(name,request),
                HttpStatus.CONTINUE
        ), HttpStatus.CONTINUE);
    }


}
