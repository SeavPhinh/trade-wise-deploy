package com.example.categoryservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySubCategoryResponse {
    private CategoryResponse categoryResponse;
    private SubCategoryResponse subCategory;
}
