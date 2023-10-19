package com.example.manageuserservice.repository;

import com.example.manageuserservice.model.BuyerFavorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BuyerFavoriteRepository extends JpaRepository<BuyerFavorite, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM buyer_favorite WHERE user_id = :#{#id} AND shop_id = :#{#shopId}", nativeQuery = true)
    BuyerFavorite findByUserIdAndShopId(UUID shopId, UUID id);

    @Transactional
    @Query(value = "SELECT * FROM buyer_favorite WHERE user_id = :#{#id}", nativeQuery = true)
    List<BuyerFavorite> findByOwnerId(UUID id);

    @Transactional
    @Query(value = "SELECT * FROM buyer_favorite WHERE shop_id = :#{#id} AND user_id = :#{#ownerId}", nativeQuery = true)
    BuyerFavorite findByShopIdAndOwnerId(UUID id, UUID ownerId);
}
