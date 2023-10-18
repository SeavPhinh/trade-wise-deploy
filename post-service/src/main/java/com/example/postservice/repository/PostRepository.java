package com.example.postservice.repository;

import com.example.postservice.model.Post;
import com.example.postservice.response.PostResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {


    @Transactional
    @Query("SELECT p from Post p where p.status = false ")
    List<Post> getAllDraftPosts();


    @Transactional
    @Query("SELECT p from Post p where p.status = true ")
    List<Post> findAllPosts();
    @Transactional
    @Query("SELECT p from Post p where p.status = true and p.id= :id  ")
    Post findPostById(@Param("id") UUID id);
    @Transactional
    @Query("SELECT p from Post p where p.status = false and p.id= :id  ")
    Post findDraftedPostById(@Param("id") UUID id);
}
