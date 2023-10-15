package com.example.postservice.service;

import com.example.postservice.request.FileRequest;
import com.example.postservice.request.PostRequest;
import com.example.postservice.response.PostResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface PostService {

    PostResponse createPost(PostRequest postRequest) throws IOException;

    List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException;

    List<PostResponse> getAllPost();

    PostResponse getPostById(UUID id);

    PostResponse deletePostById(UUID id);

    PostResponse updatePostById(UUID id, PostRequest request);
}
