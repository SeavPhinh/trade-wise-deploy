package com.example.buyerservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private String title;
    private List<String> file;
    private String description;
    private UUID subCategoryId;
    private UUID userId;
    private Boolean status;

}
