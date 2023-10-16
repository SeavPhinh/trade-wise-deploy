package com.example.productservice.repository;

import com.example.productservice.model.Product;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM products WHERE shop_id = :#{#shopId}", nativeQuery = true)
    List<Product> getAllProductByShopId(UUID shopId);
}
