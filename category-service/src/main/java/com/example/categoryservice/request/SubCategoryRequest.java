package com.example.categoryservice.request;

import com.example.categoryservice.model.Category;
import com.example.categoryservice.model.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryRequest {
    private String name;

    public SubCategory toEntity(Category category){
        return new SubCategory(null,this.name,category);
    }
}
