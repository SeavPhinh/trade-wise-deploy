package com.example.postservice.service;

import com.example.commonservice.response.FileResponse;
import com.example.postservice.exception.CustomErrorResponse;
import com.example.postservice.model.Post;
import com.example.postservice.request.FileRequest;
import com.example.postservice.request.PostRequest;
import com.example.postservice.request.RangeBudget;
import com.example.postservice.response.PostResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface PostService {

    PostResponse createPost(PostRequest postRequest) throws Exception;

    FileResponse saveListFile(MultipartFile file, HttpServletRequest request) throws Exception;

    List<PostResponse> getAllPost();

    PostResponse getPostById(UUID id);

    String deletePostById(UUID id);

    PostResponse updatePostById(UUID id, PostRequest request);

    List<PostResponse> getAllDraftPosts();

    PostResponse getDraftedPostById(UUID id);

    ByteArrayResource getImage(String fileName) throws IOException;

    List<PostResponse> findByBudgetFromAndBudgetTo(Float budgetFrom, Float budgetTo);

    List<PostResponse> getAllPostSortedByNewest();

    List<PostResponse> getAllPostSortedByOldest();

    List<PostResponse> getAllPostSortedByAZ();
    List<PostResponse> getAllPostSortedByZA();

    List<PostResponse> getAllPostSortedBySubCategory(String subCategory);

    List<PostResponse> searchPostBySubCategory(String subCategory);

    List<PostResponse> getPostsForCurrentUser();
}
