package com.example.commonservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private UUID id;
    private String title;
    private String file;
    private String description;
    private Float budget;
    private UUID subCategoryId;
    private Boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private UUID userId;
}
