package com.example.productforsaleservice.model;

import com.example.productforsaleservice.response.ProductForSaleResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_for_sales")
public class ProductForSale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    private String file;
    private String description;
    private Boolean status;
    private UUID shopId;
    private UUID postId;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

    public ProductForSaleResponse toDto(List<String> files){
        return new ProductForSaleResponse(this.id,this.title,files,this.description,this.status,this.shopId,this.postId,this.createdDate,this.lastModified);
    }

}
