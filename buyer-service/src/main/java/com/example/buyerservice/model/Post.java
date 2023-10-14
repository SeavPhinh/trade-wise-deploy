package com.example.buyerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
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
    private UUID subCategoryId;
    private Boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;
    private UUID userId;

}
