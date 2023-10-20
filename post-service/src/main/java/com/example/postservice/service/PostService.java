package com.example.postservice.service;

import com.example.postservice.exception.CustomErrorResponse;
import com.example.postservice.model.Post;
import com.example.postservice.request.FileRequest;
import com.example.postservice.request.PostRequest;
import com.example.postservice.response.PostResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface PostService {

    PostResponse createPost(PostRequest postRequest) ;

    List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request, UUID postId) throws IOException;

    List<PostResponse> getAllPost();

    PostResponse getPostById(UUID id);

    PostResponse deletePostById(UUID id);

    PostResponse updatePostById(UUID id, PostRequest request);

    List<PostResponse> getAllDraftPosts();

    PostResponse getDraftedPostById(UUID id);

    byte[] getImageByName(String name) throws IOException;

    List<PostResponse> findByBudgetFromAndBudgetTo(Float budgetFrom, Float budgetTo);

    List<PostResponse> getAllPostSortedByNewest();

    List<PostResponse> getAllPostSortedByOldest();

    List<PostResponse> getAllPostSortedByAZ();
    List<PostResponse> getAllPostSortedByZA();

}
