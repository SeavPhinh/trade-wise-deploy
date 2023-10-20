package com.example.postservice.model;

import com.example.postservice.response.PostResponse;
import com.example.commonservice.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String title;
    @Column(nullable = false)
    private String file;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private Float budgetFrom;
    @Column(nullable = false)
    private Float budgetTo;
    private UUID subCategoryId;
    private Boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private UUID userId;

    public PostResponse toDto(List<String> files, User createdBy){
        return new PostResponse(this.id,this.title, files, this.description, this.budgetFrom,this.budgetTo, this.subCategoryId, this.status,this.createdDate,this.lastModified,createdBy);
    }


}
