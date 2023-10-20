package com.example.categoryservice.model;

import com.example.categoryservice.response.CategoryResponse;
import com.example.categoryservice.response.CategorySubCategoryResponse;
import com.example.categoryservice.response.SubCategoryResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sub_categories")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public SubCategoryResponse toDto(){
        return new SubCategoryResponse(this.id,this.name);
    }

    public CategorySubCategoryResponse toDtoSub(CategoryResponse category, SubCategoryResponse subCategoryResponse){
        return new CategorySubCategoryResponse(category,subCategoryResponse);
    }

}
