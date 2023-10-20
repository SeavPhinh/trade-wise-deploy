package com.example.categoryservice.service.subcategory;

import com.example.categoryservice.exception.NotFoundExceptionClass;
import com.example.categoryservice.model.Category;
import com.example.categoryservice.model.SubCategory;
import com.example.categoryservice.repository.CategoryRepository;
import com.example.categoryservice.repository.SubCategoryRepository;
import com.example.categoryservice.request.SubCategoryRequest;
import com.example.categoryservice.response.CategoryResponse;
import com.example.categoryservice.response.CategorySubCategory;
import com.example.categoryservice.response.CategorySubCategoryResponse;
import com.example.categoryservice.response.SubCategoryResponse;
import com.example.commonservice.config.ValidationConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubCategoryServiceImpl implements SubCategoryService{

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategorySubCategoryResponse getSubCategoryById(UUID id) {
        SubCategory subCategory = subCategoryRepository.getAllById(id);
        if(subCategory != null){
            Optional<Category> category = categoryRepository.findById(subCategory.getCategory().getId());
            if(category.isPresent()){
                return new CategorySubCategoryResponse(category.get().toDto(),subCategory.toDto());
            }
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_CATEGORIES);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
    }

    @Override
    public CategorySubCategoryResponse addSubCategory(UUID categoryId, SubCategoryRequest request) {
        CategorySubCategoryResponse response = new CategorySubCategoryResponse();

        Category category = categoryRepository.findById(categoryId).orElseThrow();
        CategoryResponse categoryResponse = category.toDto();

        SubCategory subCategory = subCategoryRepository.save(request.toEntity(category));
        SubCategoryResponse subCategoryResponse = subCategory.toDto();

        response.setCategoryResponse(categoryResponse);
        response.setSubCategory(subCategoryResponse);

        return response;
    }

    @Override
    public CategorySubCategoryResponse deleteSubCategoryById(UUID id) {
        CategorySubCategoryResponse response = new CategorySubCategoryResponse();

        Optional<SubCategory> subCategory = subCategoryRepository.findById(id);
        if(!subCategory.isPresent()){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }

        response.setCategoryResponse(categoryRepository.findById(subCategory.get().getCategory().getId()).orElseThrow().toDto());
        response.setSubCategory(subCategory.get().toDto());

        subCategoryRepository.deleteById(id);
        return response;
    }

}
