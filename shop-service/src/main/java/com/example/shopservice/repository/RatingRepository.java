package com.example.shopservice.repository;

import com.example.shopservice.model.Rating;
import com.example.shopservice.model.Shop;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE user_id = :#{#id} AND shop_id = :#{#shopId}", nativeQuery = true)
    Rating getRatingRecordByOwnerId(UUID id, UUID shopId);

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE level = 'ONE_STAR'", nativeQuery = true)
    List<Rating> getAllShopRatedOneStar();

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE level = 'TWO_STARS'", nativeQuery = true)
    List<Rating> getAllShopRatedTwoStars();

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE level = 'THREE_STARS'", nativeQuery = true)
    List<Rating> getAllShopRatedThreeStars();

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE level = 'FOUR_STARS'", nativeQuery = true)
    List<Rating> getAllShopRatedFourStars();

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE level = 'FIVE_STARS'", nativeQuery = true)
    List<Rating> getAllShopRatedFiveStars();

    default List<Rating> getAllShopRatedByStars(String ratingLevel) {
        switch (ratingLevel) {
            case "ONE_STAR":
                return getAllShopRatedOneStar();
            case "TWO_STARS":
                return getAllShopRatedTwoStars();
            case "THREE_STARS":
                return getAllShopRatedThreeStars();
            case "FOUR_STARS":
                return getAllShopRatedFourStars();
            case "FIVE_STARS":
                return getAllShopRatedFiveStars();
            default:
                throw new IllegalArgumentException("Invalid rating level: " + ratingLevel);
        }
    }

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE shop_id = :#{#id} AND level = :#{#level}", nativeQuery = true)
    List<Rating> countRatedRecordByProjectId(UUID id, String level);

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE shop_id = :#{#id}", nativeQuery = true)
    List<Rating> countRatedRecordByShopId(UUID id);


    @Transactional
    @Query(value = "SELECT shop_id FROM ratings", nativeQuery = true)
    List<UUID> getAllShopIdFromRating();

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE shop_id = :#{#shopId} AND level = :#{#name}", nativeQuery = true)
    List<Rating> getAllShopRatedByStarsAndActiveShop(String name, UUID shopId);

    @Transactional
    @Query(value = "SELECT level FROM ratings WHERE shop_id = :#{#id}", nativeQuery = true)
    List<String> getRatedStarByShopId(UUID id);
}
