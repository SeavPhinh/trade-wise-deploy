package com.example.shopservice.repository;

import com.example.shopservice.model.Rating;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RatingRepository extends JpaRepository<Rating, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM ratings WHERE user_id = :#{#id}", nativeQuery = true)
    Rating getRatingRecordByOwnerId(UUID id);

}
