package com.example.buyerservice.controller;

import com.example.buyerservice.model.Post;
import com.example.buyerservice.request.PostRequest;
import com.example.buyerservice.service.PostService;
import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@Tag(name = "Post")
@SecurityRequirement(name = "oAuth2")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/post")
    @Operation(summary = "user created post")
    public ResponseEntity<ApiResponse<Post>> createPost(@Valid @RequestBody PostRequest postRequest) throws IOException {
        return new ResponseEntity<>(new ApiResponse<>(
                "User created post successfully",
                postService.createPost(postRequest),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload multiple file")
    public ResponseEntity<?> saveMultiFile(@RequestParam(required = false) List<MultipartFile> files,
                                           HttpServletRequest request) throws IOException {
        if(files != null){
            return ResponseEntity.status(200).body(postService.saveListFile(files,request));
        }
        throw new NotFoundExceptionClass("No filename to upload");
    }


}
