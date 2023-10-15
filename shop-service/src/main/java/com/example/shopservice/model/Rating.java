package com.example.shopservice.model;

import com.example.shopservice.enumeration.Level;
import com.example.shopservice.response.RatingResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    @Enumerated(EnumType.STRING)
    private Level level;
    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    public RatingResponse toDto(UUID shopId){
        return new RatingResponse(this.userId,this.level,shopId);
    }

}
