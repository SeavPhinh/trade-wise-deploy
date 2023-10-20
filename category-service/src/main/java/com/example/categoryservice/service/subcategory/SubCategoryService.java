package com.example.categoryservice.service.subcategory;

import com.example.categoryservice.request.SubCategoryRequest;
import com.example.categoryservice.response.CategorySubCategoryResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface SubCategoryService {
    CategorySubCategoryResponse getSubCategoryById(UUID name);

    CategorySubCategoryResponse addSubCategory(UUID categoryId, SubCategoryRequest request);

    CategorySubCategoryResponse deleteSubCategoryById(UUID id);
}
