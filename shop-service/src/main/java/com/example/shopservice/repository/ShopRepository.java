package com.example.shopservice.repository;

import com.example.shopservice.model.Shop;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopRepository extends JpaRepository<Shop, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE user_id = :#{#ownerId}", nativeQuery = true)
    Shop getShopByOwnerId(UUID ownerId);

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE status = true", nativeQuery = true)
    List<Shop> getAllActiveShop();

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE status = true AND id= :#{#id}", nativeQuery = true)
    Shop getActiveShopById(UUID id);
}
