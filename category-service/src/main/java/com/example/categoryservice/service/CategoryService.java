package com.example.categoryservice.service;

import com.example.categoryservice.request.CategoryRequest;
import com.example.categoryservice.response.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    CategoryResponse addCategory(CategoryRequest request);

    CategoryResponse getCategoryById(UUID id);

    CategoryResponse deleteCategoryById(UUID id);

    CategoryResponse updateCategoryById(UUID id,CategoryRequest request);
}
