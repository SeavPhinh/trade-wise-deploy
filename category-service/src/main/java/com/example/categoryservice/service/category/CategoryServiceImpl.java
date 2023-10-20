package com.example.categoryservice.service.category;

import com.example.categoryservice.exception.NotFoundExceptionClass;
import com.example.categoryservice.model.Category;
import com.example.categoryservice.model.SubCategory;
import com.example.categoryservice.repository.CategoryRepository;
import com.example.categoryservice.repository.SubCategoryRepository;
import com.example.categoryservice.request.CategoryRequest;
import com.example.categoryservice.response.CategoryResponse;
import com.example.categoryservice.response.CategorySubCategory;
import com.example.categoryservice.response.SubCategoryResponse;
import com.example.commonservice.config.ValidationConfig;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    public CategoryServiceImpl(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<CategoryResponse> categoryResponseList = categoryRepository.findAll().stream().map(Category::toDto).collect(Collectors.toList());
        if(!categoryResponseList.isEmpty()){
            return categoryResponseList;
        }
        throw new NotFoundExceptionClass(ValidationConfig.EMPTY_CATEGORIES);
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {
        List<Category> lists = categoryRepository.findAll();
        for (Category category: lists) {
            if(category.getName().equalsIgnoreCase(request.getName())){
                throw new IllegalArgumentException(ValidationConfig.EXISTING_CATEGORIES);
            }
        }
        return categoryRepository.save(request.toEntity()).toDto();
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            return category.get().toDto();
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_CATEGORIES);
    }

    @Override
    public CategoryResponse deleteCategoryById(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            categoryRepository.deleteById(id);
            return category.get().toDto();
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_CATEGORIES);

    }

    @Override
    public CategoryResponse updateCategoryById(UUID id,CategoryRequest request) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){

            List<Category> lists = categoryRepository.findAll();
            for (Category categories: lists) {
                if(categories.getName().equalsIgnoreCase(request.getName())){
                    throw new IllegalArgumentException(ValidationConfig.EXISTING_CATEGORIES);
                }
            }
            category.get().setName(request.getName());
            return categoryRepository.save(category.get()).toDto();
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_CATEGORIES);
    }

    @Override
    public CategorySubCategory getCategoryAndSubCategoryById(UUID id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            List<SubCategory> subCategory = subCategoryRepository.getAllSubCategoryByCategoryId(id);
            if(subCategory.isEmpty()){
                throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
            }
            List<SubCategoryResponse> listSubCategories = subCategory.stream().map(SubCategory::toDto).collect(Collectors.toList());
            return new CategorySubCategory(category.get().getId(),category.get().getName(),listSubCategories);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_CATEGORIES);
    }
}
