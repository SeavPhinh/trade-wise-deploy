package com.example.manageuserservice.model;
import com.example.commonservice.response.PostResponse;
import com.example.manageuserservice.response.SellerFavoriteResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seller_favorite")
public class SellerFavorite {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private UUID postId;
    public SellerFavoriteResponse toDto(PostResponse post){
        return new SellerFavoriteResponse(this.id,this.userId,post);
    }
}
