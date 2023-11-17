package com.example.notificationservice.repository;

import com.example.notificationservice.model.PostNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PostNotificationRepository extends JpaRepository<PostNotification, UUID> {

}
