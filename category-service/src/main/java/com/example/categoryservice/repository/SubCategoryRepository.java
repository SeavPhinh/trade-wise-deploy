package com.example.categoryservice.repository;

import com.example.categoryservice.model.SubCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM sub_categories WHERE category_id = :#{#id}", nativeQuery = true)
    List<SubCategory> getAllSubCategoryByCategoryId(UUID id);

    @Transactional
    @Query(value = "SELECT * FROM sub_categories WHERE name = :#{#name}", nativeQuery = true)
    SubCategory getAllByName(String name);

    @Transactional
    @Query(value = "DELETE * FROM sub_categories WHERE name = :#{#name}", nativeQuery = true)
    void removeSubCategoryByName(String name);
}
