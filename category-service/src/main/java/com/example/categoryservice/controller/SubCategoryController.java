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

import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@Tag(name = "SubCategory")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @GetMapping("/subcategories/{id}")
    @Operation(summary = "fetch sub category by sub category id")
    public ResponseEntity<ApiResponse<CategorySubCategoryResponse>> getSubCategoryById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "SubCategories fetched by id successfully",
                subCategoryService.getSubCategoryById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/subcategories/{categoryId}")
    @Operation(summary = "adding subcategory by category id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategorySubCategoryResponse>> addSubCategory(@PathVariable UUID categoryId,
                                                                                   @Valid @RequestBody SubCategoryRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "SubCategories added successfully",
                subCategoryService.addSubCategory(categoryId,request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/subcategories/{id}")
    @Operation(summary = "delete sub categories by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<CategorySubCategoryResponse>> deleteSubCategoryById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "SubCategories delete by id successfully",
                subCategoryService.deleteSubCategoryById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

//
//    @PutMapping("/subcategories/{id}")
//    @Operation(summary = "update categories by id")
//    @SecurityRequirement(name = "oAuth2")
//    public ResponseEntity<ApiResponse<CategoryResponse>> updateSubCategoryById(@PathVariable UUID id,
//                                                                            @Valid @RequestBody CategoryRequest request){
//        return new ResponseEntity<>(new ApiResponse<>(
//                "SubCategories updated by id successfully",
//                subCategoryService.updateSubCategoryById(id,request),
//                HttpStatus.OK
//        ), HttpStatus.OK);
//    }


}
