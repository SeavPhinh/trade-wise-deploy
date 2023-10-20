package com.example.categoryservice.response;

import com.example.categoryservice.model.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategorySubCategory {

    private UUID id;
    private String name;
    private List<SubCategoryResponse> subCategory;

}
