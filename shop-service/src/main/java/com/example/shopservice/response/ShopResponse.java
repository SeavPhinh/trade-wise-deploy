package com.example.shopservice.response;

import com.example.shopservice.model.Address;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopResponse implements Serializable{

    private UUID id;
    private String name;
    private String profileImage;
    private UUID userId;
    private Boolean status;
    private Integer ratedCount;
    private Float rated;
    private List<String> subCategoryList;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Address address;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;



}
