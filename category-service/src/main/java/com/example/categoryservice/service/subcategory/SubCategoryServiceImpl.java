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
        List<SubCategory> subCat = subCategoryRepository.findAll();
        for (SubCategory sub: subCat) {
            if(sub.getName().equalsIgnoreCase(request.getName())){
                throw new IllegalArgumentException(ValidationConfig.EXISTING_SUB_CATEGORIES);
            }
        }
        Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_CATEGORIES);
        }

        return new CategorySubCategoryResponse(category.get().toDto(),subCategoryRepository.save(request.toEntity(category.get())).toDto());
    }

    @Override
    public CategorySubCategoryResponse deleteSubCategoryById(UUID id) {
        Optional<SubCategory> subCategory = subCategoryRepository.findById(id);
        if(subCategory.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
        subCategoryRepository.deleteById(id);
        return new CategorySubCategoryResponse(categoryRepository.findById(subCategory.get().getCategory().getId()).orElseThrow().toDto(),subCategory.get().toDto());
    }

    @Override
    public CategorySubCategoryResponse updateSubCategoryById(UUID id, SubCategoryRequest request) {
        List<SubCategory> subCat = subCategoryRepository.findAll();
        for (SubCategory sub: subCat) {
            if(sub.getName().equalsIgnoreCase(request.getName())){
                throw new IllegalArgumentException(ValidationConfig.EXISTING_SUB_CATEGORIES);
            }
        }
        Optional<SubCategory> subCategory = subCategoryRepository.findById(id);
        if(subCategory.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
        subCategory.get().setName(request.getName());
        subCategoryRepository.save(subCategory.get());
        return new CategorySubCategoryResponse(categoryRepository.findById(subCategory.get().getCategory().getId()).orElseThrow().toDto(),subCategory.get().toDto());
    }

}
