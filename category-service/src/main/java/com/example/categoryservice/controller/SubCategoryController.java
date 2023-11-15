package com.example.categoryservice.controller;

import com.example.categoryservice.request.SubCategoryRequest;
import com.example.categoryservice.response.CategorySubCategoryResponse;
import com.example.categoryservice.service.subcategory.SubCategoryService;
import com.example.commonservice.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/sub-categories")
@Tag(name = "SubCategory")
@CrossOrigin
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @GetMapping("")
    @Operation(summary = "*fetch sub category by sub category name")
    public ResponseEntity<ApiResponse<CategorySubCategoryResponse>> getSubCategoryById(@RequestParam String name){
        return new ResponseEntity<>(new ApiResponse<>(
                "SubCategories fetched by id successfully",
                subCategoryService.getSubCategoryByName(name),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "*adding subcategory by category name")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategorySubCategoryResponse>> addSubCategory(@RequestParam String categoryName,
                                                                                   @Valid @RequestBody SubCategoryRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "SubCategories added successfully",
                subCategoryService.addSubCategory(categoryName,request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping("")
    @Operation(summary = "*delete sub categories by name")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategorySubCategoryResponse>> deleteSubCategoryByName(@RequestParam String name){
        return new ResponseEntity<>(new ApiResponse<>(
                "SubCategories delete by name successfully",
                subCategoryService.deleteSubCategoryByName(name),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }


    @PutMapping("")
    @Operation(summary = "*update subcategories by name")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategorySubCategoryResponse>> updateSubCategoryByName(@RequestParam String name,
                                                                            @Valid @RequestBody SubCategoryRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "SubCategories updated by name successfully",
                subCategoryService.updateSubCategoryByName(name,request),
                HttpStatus.CONTINUE
        ), HttpStatus.CONTINUE);
    }


}
