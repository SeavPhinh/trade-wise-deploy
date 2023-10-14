package com.example.buyerservice.service;

import com.example.buyerservice.model.Post;
import com.example.buyerservice.request.FileRequest;
import com.example.buyerservice.request.PostRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface PostService {

    Post createPost(PostRequest postRequest) throws IOException;

    List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException;
}
