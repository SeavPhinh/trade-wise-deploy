package com.example.categoryservice.repository;

import com.example.categoryservice.model.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {


    @Transactional
    @Query(value = "SELECT * FROM categories WHERE name = :#{#name}", nativeQuery = true)
    Category getCategoryByName(String name);

    @Transactional
    @Query(value = "DELETE * FROM categories WHERE name = :#{#name}", nativeQuery = true)
    void removeCategoryName(String name);
}
