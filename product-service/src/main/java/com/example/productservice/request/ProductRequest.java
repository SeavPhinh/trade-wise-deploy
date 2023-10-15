package com.example.productservice.request;

import com.example.productservice.model.Product;
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

    private String title;
    private List<String> files;
    private String description;
    private Float price;
    private Boolean status;

    public Product toEntity(UUID shopId){
        return new Product(null,this.title,this.files.toString(),this.description,this.price,this.status, LocalDateTime.now(),LocalDateTime.now(),shopId);
    }

}
