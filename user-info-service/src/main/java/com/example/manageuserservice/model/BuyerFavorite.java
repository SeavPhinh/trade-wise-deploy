package com.example.manageuserservice.model;
import com.example.commonservice.response.ShopResponse;
import com.example.manageuserservice.response.BuyerFavoriteResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "buyer_favorite")
public class BuyerFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private UUID shopId;

    public BuyerFavoriteResponse toDto(ShopResponse shop, String profileImage){
        return new BuyerFavoriteResponse(this.id,this.userId,shop,profileImage);
    }

}
