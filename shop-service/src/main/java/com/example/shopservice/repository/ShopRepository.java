package com.example.shopservice.repository;

import com.example.shopservice.model.Shop;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE user_id = :#{#ownerId}", nativeQuery = true)
    Shop getShopByOwnerId(UUID ownerId);
}
