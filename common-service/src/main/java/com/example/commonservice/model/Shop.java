package com.example.commonservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shop {

    private UUID id;
    private String name;
    private String profileImage;
    private UUID userId;
    private Boolean status;
    private Address address;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

}
