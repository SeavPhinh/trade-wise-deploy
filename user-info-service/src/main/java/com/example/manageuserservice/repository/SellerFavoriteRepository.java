package com.example.manageuserservice.repository;

import com.example.manageuserservice.model.SellerFavorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellerFavoriteRepository extends JpaRepository<SellerFavorite, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM seller_favorite WHERE user_id = :#{#id} AND post_id = :#{#postId}", nativeQuery = true)
    SellerFavorite findByUserIdAndPostId(UUID postId, UUID id);

    @Transactional
    @Query(value = "SELECT * FROM seller_favorite WHERE user_id = :#{#id}", nativeQuery = true)
    List<SellerFavorite> findByOwnerId(UUID id);

    @Transactional
    @Query(value = "SELECT * FROM seller_favorite WHERE post_id = :#{#id} AND user_id = :#{#ownerId}", nativeQuery = true)
    SellerFavorite findByPostIdAndOwnerId(UUID id, UUID ownerId);

}
