package com.example.productforsaleservice.request;

import com.example.productforsaleservice.model.ProductForSale;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductForSaleRequest {
    private String title;
    private List<String> files;
    private String description;
    private Boolean status;
    private UUID shopId;
    private UUID postId;

    public ProductForSale toEntity(){
        return new ProductForSale(null, this.title,this.files.toString(),this.description,this.status,this.shopId,this.postId, LocalDateTime.now(), LocalDateTime.now());
    }

}
