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
public class ProductResponse {

    private UUID id;
    private String title;
    private List<String> file;
    private String description;
    private String subCategory;
    private Float price;
    private Integer quantity;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private UUID shopId;

}
