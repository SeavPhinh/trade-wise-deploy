package com.example.categoryservice.service.subcategory;

import com.example.categoryservice.request.SubCategoryRequest;
import com.example.categoryservice.response.CategorySubCategoryResponse;
import org.springframework.stereotype.Service;

@Service
public interface SubCategoryService {
    CategorySubCategoryResponse getSubCategoryByName(String name);

    CategorySubCategoryResponse addSubCategory(String categoryName, SubCategoryRequest request);

    CategorySubCategoryResponse deleteSubCategoryByName(String name);

    CategorySubCategoryResponse updateSubCategoryByName(String name, SubCategoryRequest request);
}
