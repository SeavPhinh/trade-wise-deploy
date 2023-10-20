package com.example.categoryservice.model;

import com.example.categoryservice.response.CategoryResponse;
import com.example.categoryservice.response.CategorySubCategory;
import com.example.categoryservice.response.SubCategoryResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true)
    private String name;

    public CategoryResponse toDto(){
        return new CategoryResponse(this.id,this.name);
    }
    public CategorySubCategory csToDto(List<SubCategoryResponse> response){
        return new CategorySubCategory(this.id,this.name,response);
    }
}
