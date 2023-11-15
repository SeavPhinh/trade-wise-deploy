package com.example.productservice.request;

import com.example.commonservice.config.ValidationConfig;
import com.example.productservice.model.Product;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    @NotEmpty(message = ValidationConfig.POST_TITLE_REQUIRE)
    @NotNull(message = ValidationConfig.NULL_TITLE)
    @Size(min = 5, max = 25, message = ValidationConfig.POST_TITLE_MESSAGE)
    private String title;

    @NotNull(message = ValidationConfig.NULL_FILE)
    @NotEmpty(message = ValidationConfig.EMPTY_FILE)
    @Size(min = 1 , max = 6, message = ValidationConfig.COUNT_IMAGE)
    private List<String> files;

    @NotNull(message = ValidationConfig.NULL_DESCRIPTION)
    @NotEmpty(message = ValidationConfig.EMPTY_DESCRIPTION)
    private String description;

    @NotNull(message = ValidationConfig.NULL_SUB_CATEGORY)
    @NotEmpty(message = ValidationConfig.EMPTY_SUB_CATEGORY)
    private String subCategory;

    @NotNull(message = ValidationConfig.NULL_PRICE)
    @DecimalMin(value = "0.0", message = ValidationConfig.INVALID_PRICE)
    private Float price;

    @NotNull(message = ValidationConfig.NULL_QUANTITY)
    @DecimalMin(value = "0", message = ValidationConfig.INVALID_QUANTITY)
    private Integer quantity;

    public Product toEntity(UUID shopId){
        return new Product(null,this.title.trim(),this.files.toString(),this.description.trim(),this.subCategory,this.price,this.quantity, LocalDateTime.now(),LocalDateTime.now(),shopId);
    }

}
