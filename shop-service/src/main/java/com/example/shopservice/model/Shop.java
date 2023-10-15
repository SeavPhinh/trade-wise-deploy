package com.example.shopservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String profileImage;
    private UUID userId;
    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;

    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

}
