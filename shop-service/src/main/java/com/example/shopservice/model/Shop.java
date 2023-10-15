package com.example.shopservice.model;

import com.example.shopservice.response.ShopResponse;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

    public ShopResponse toDto(){
        return new ShopResponse(this.id,this.name,this.profileImage,this.userId,this.address,this.createdDate,this.lastModified);
    }
}
