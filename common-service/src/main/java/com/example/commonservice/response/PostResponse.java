package com.example.commonservice.response;
import com.example.commonservice.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private UUID id;
    private String title;
    private List<String> file;
    private String description;
    private Float budget;
    private UUID subCategoryId;
    private Boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private User createdBy;

}
