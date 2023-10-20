package com.example.categoryservice.request;

import com.example.categoryservice.model.Category;
import com.example.commonservice.config.ValidationConfig;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

    @NotEmpty(message = ValidationConfig.EMPTY_FIELD)
    @NotNull(message = ValidationConfig.NULL_FIELD)
    private String name;

    public Category toEntity(){
        return new Category(null,this.name.trim().toUpperCase());
    }
}
