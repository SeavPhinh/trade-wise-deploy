package com.example.notificationservice.repository;

import com.example.notificationservice.model.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM notifications WHERE sub_category = :#{#category}", nativeQuery = true)
    List<Notification> getAllBySubCategory(String category);
}
