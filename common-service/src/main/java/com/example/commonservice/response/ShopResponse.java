package com.example.commonservice.response;

import com.example.commonservice.model.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopResponse {

    private UUID id;
    private String name;
    private String profileImage;
    private UUID userId;
    private Boolean status;
    private Integer ratedCount;
    private Float rated;
    private List<String> subCategoryList;
    private Address address;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;



}
