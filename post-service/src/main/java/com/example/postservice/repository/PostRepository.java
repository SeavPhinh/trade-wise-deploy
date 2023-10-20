package com.example.postservice.repository;

import com.example.postservice.model.FileStorage;
import com.example.postservice.model.Post;
import com.example.postservice.response.PostResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
    @Query("SELECT p from Post p where p.status = true and p.userId= :currentUserId")
    List<Post> findAllPostForCurrentUser(UUID currentUserId);

    @Transactional
    @Query("SELECT p from Post p where p.status = true and p.id= :id  ")
    Post findPostById(@Param("id") UUID id);
    @Transactional
    @Query("SELECT p from Post p where p.status = false and p.id= :id  ")
    Post findDraftedPostById(@Param("id") UUID id);


    @Transactional
    @Query("SELECT p from Post p where p.status = true and (p.budgetFrom >= :budgetFrom AND p.budgetFrom <= :budgetTo) or (p.budgetFrom <= :budgetFrom and  p.budgetTo >= :budgetFrom )  ")
    List<Post> findByBudgetFromAndBudgetTo( @Param("budgetFrom") Float budgetFrom, @Param("budgetTo") Float budgetTo);


    @Transactional
    @Query("select p from Post p  where p.status = true order by p.createdDate desc ")
    List<Post> findAllSortedByNewest();

    @Transactional
    @Query("select p from Post p  where p.status = true order by p.createdDate asc ")
    List<Post> findAllSortedByOldest();

    @Transactional
    @Query("select p from Post p  where p.status = true order by p.title asc ")
    List<Post> findAllSortedByAZ();

    @Transactional
    @Query("select p from Post p  where p.status = true order by p.title desc ")
    List<Post> findAllSortedByZA();

    @Transactional
    @Query("select p from Post p  where  p.status = true and p.subCategory= :subCategory")
    List<Post> getAllPostSortedBySubCategory(String subCategory);


    @Transactional
    @Query("select p from Post p  where p.status = true and p.subCategory like %:subCategory% ")
    List<Post> searchPostBySubCategory(String subCategory);
}
