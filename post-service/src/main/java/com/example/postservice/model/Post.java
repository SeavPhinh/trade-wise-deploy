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
    private String subCategory;
    private Boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private UUID userId;

    public PostResponse toDto(User createdBy, String profileImage){
        return new PostResponse(this.id,this.title, this.file, this.description, this.budgetFrom,this.budgetTo, this.subCategory, this.status,this.createdDate,this.lastModified,createdBy, profileImage);
    }


}
