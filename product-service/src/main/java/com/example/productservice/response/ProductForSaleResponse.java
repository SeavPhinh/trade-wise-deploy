package com.example.productservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductForSaleResponse {

    private UUID id;
    private String title;
    private List<String> files;
    private String description;
    private Float price;
    private Boolean status;
    private UUID shopId;
    private UUID postId;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

}
