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

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE status = true AND sub_category_list = :#{#name}", nativeQuery = true)
    List<Shop> getAllBasedOnFilter(String name);

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE status = true order by created_date asc", nativeQuery = true)
    List<Shop> getOldestShop();

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE status = true order by created_date desc ", nativeQuery = true)
    List<Shop> getNewestShop();

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE status = true order by name asc ", nativeQuery = true)
    List<Shop> getAZShop();

    @Transactional
    @Query(value = "SELECT * FROM shops WHERE status = true order by name desc ", nativeQuery = true)
    List<Shop> getZAShop();
}
