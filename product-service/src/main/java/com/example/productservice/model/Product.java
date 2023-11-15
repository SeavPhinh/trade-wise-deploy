package com.example.productservice.model;

import com.example.productservice.response.ProductResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    @Column(length = 1000000)
    private String file;
    @Column(length = 1000000)
    private String description;
    private String subCategory;
    private Float price;
    private Integer quantity;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private UUID shopId;

    public ProductResponse toDto(List<String> files){
        return new ProductResponse(this.id,this.title,files,this.description,this.subCategory,this.price,this.quantity,this.createdDate,this.lastModified,this.shopId);
    }

}
