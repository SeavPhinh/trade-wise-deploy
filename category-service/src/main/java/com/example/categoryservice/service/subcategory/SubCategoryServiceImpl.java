package com.example.categoryservice.service.subcategory;

import com.example.categoryservice.exception.NotFoundExceptionClass;
import com.example.categoryservice.model.Category;
import com.example.categoryservice.model.SubCategory;
import com.example.categoryservice.repository.CategoryRepository;
import com.example.categoryservice.repository.SubCategoryRepository;
import com.example.categoryservice.request.SubCategoryRequest;
import com.example.categoryservice.response.CategorySubCategoryResponse;
import com.example.commonservice.config.ValidationConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubCategoryServiceImpl implements SubCategoryService{

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository, CategoryRepository categoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategorySubCategoryResponse getSubCategoryByName(String name) {
        SubCategory subCategory = subCategoryRepository.getAllByName(name);
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
    public CategorySubCategoryResponse addSubCategory(String name, SubCategoryRequest request) {
        List<SubCategory> subCat = subCategoryRepository.findAll();
        for (SubCategory sub: subCat) {
            if(sub.getName().equalsIgnoreCase(request.getName())){
                throw new IllegalArgumentException(ValidationConfig.EXISTING_SUB_CATEGORIES);
            }
        }
        Category category = categoryRepository.getCategoryByName(name);
        if(category == null){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_CATEGORIES);
        }

        return new CategorySubCategoryResponse(category.toDto(),subCategoryRepository.save(request.toEntity(category)).toDto());
    }

    @Override
    public CategorySubCategoryResponse deleteSubCategoryByName(String name) {
        SubCategory subCategory = subCategoryRepository.getAllByName(name);
        if(subCategory == null){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
        subCategoryRepository.removeSubCategoryByName(name);
        return new CategorySubCategoryResponse(categoryRepository.findById(subCategory.getCategory().getId()).orElseThrow().toDto(),subCategory.toDto());
    }

    @Override
    public CategorySubCategoryResponse updateSubCategoryByName(String name, SubCategoryRequest request) {
        List<SubCategory> subCat = subCategoryRepository.findAll();
        for (SubCategory sub: subCat) {
            if(sub.getName().equalsIgnoreCase(request.getName())){
                throw new IllegalArgumentException(ValidationConfig.EXISTING_SUB_CATEGORIES);
            }
        }
        SubCategory subCategory = subCategoryRepository.getAllByName(name);
        if(subCategory == null){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
        subCategory.setName(request.getName());
        subCategoryRepository.save(subCategory);
        return new CategorySubCategoryResponse(categoryRepository.findById(subCategory.getCategory().getId()).orElseThrow().toDto(),subCategory.toDto());
    }

}
