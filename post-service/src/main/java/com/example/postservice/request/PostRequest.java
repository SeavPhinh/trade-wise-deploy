package com.example.postservice.request;

import com.example.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private String title;
    private List<String> file;
    private String description;
    private Double budget;
    private UUID subCategoryId;
    private Boolean status;


    public Post toEntity(UUID userId){
        return new Post(null,this.title,this.file.toString(),this.description, this.budget,this.subCategoryId,this.status, LocalDateTime.now(),LocalDateTime.now(),userId);
    }

}
