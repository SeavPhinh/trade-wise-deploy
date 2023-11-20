package com.example.notificationservice.repository;
import com.example.notificationservice.model.Notification;
import com.example.notificationservice.model.PostNotification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PostNotificationRepository extends JpaRepository<PostNotification, UUID> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO post_notifications (id,post_id,user_id,message,shop,date,profile_shop, status) VALUES (:#{#notify.id},:#{#notify.postId},:#{#notify.userId},:#{#notify.message},:#{#notify.shop},:#{#notify.date},:#{#notify.profileShop},:#{#notify.status})", nativeQuery = true)
    void persistData(@Param("notify") PostNotification notify);

    @Transactional
    @Modifying
    @Query(value = "SELECT * FROM post_notifications WHERE user_id = :#{#userId}", nativeQuery = true)
    List<PostNotification> getAllNotificationByPostId(@Param("userId") UUID userId);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT count(*) FROM post_notifications WHERE status = false AND user_id = :#{#userId}")
    Integer countUnreadComment(@Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE post_notifications SET status = true WHERE id = :#{#id} AND user_id = :#{#userId}")
    void readCommentNotification(@Param("id") UUID id,  @Param("userId") UUID userId);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM post_notifications WHERE id = :#{#id})")
    Boolean checkCommentId(@Param("id") UUID id);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT EXISTS(SELECT 1 FROM post_notifications WHERE user_id = :#{#userId})")
    Boolean checkUserById(@Param("userId") UUID userId);

    @Transactional
    @Query(nativeQuery = true, value = "SELECT * FROM post_notifications WHERE id = :#{#id} AND user_id = :#{#userId}")
    PostNotification getNotificationByUserIdAndId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM post_notifications WHERE id = :#{#id} AND user_id = :#{#userId}")
    void deleteCommentNotificationByUserIdAndId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM post_notifications WHERE user_id = :#{#userId}")
    void deleteNotificationByUserId(@Param("userId") UUID userId);
}
