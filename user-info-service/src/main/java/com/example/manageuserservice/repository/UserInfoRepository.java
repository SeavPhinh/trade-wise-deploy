package com.example.manageuserservice.repository;

import com.example.manageuserservice.model.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, UUID> {

    @Transactional
    @Query(value = "SELECT * FROM user_info WHERE user_id = :#{#id}", nativeQuery = true)
    UserInfo findByOwnerId(UUID id);

}
