package com.example.commonservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySubCategoryResponse {
    private CategoryResponse categoryResponse;
    private SubCategoryResponse subCategory;
}
