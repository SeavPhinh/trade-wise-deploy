package com.example.postservice.controller;

import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.response.ApiResponse;
import com.example.postservice.request.PostRequest;
import com.example.postservice.response.PostResponse;
import com.example.postservice.service.PostService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/operation")
@Tag(name = "Crud Post Operation")
@CrossOrigin
public class CrudPostController {

    private final PostService postService;

    public CrudPostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("")
    @Operation(summary = "BUYER created post")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody PostRequest postRequest) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                "BUYER created new post successfully",
                postService.createPost(postRequest),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "delete post or drafted by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<?>> deletePostById(@PathVariable UUID id) {
        return new ResponseEntity<>(new ApiResponse<>(
                "post delete by id successfully",
                postService.deletePostById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update post and drafted post by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<PostResponse>> updatePostById(@PathVariable UUID id,
                                                                    @Valid @RequestBody PostRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                " updated post by id successfully",
                postService.updatePostById(id, request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload file to post")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<?> saveMultiFile(@RequestParam(required = false) MultipartFile file,
                                           HttpServletRequest request) throws Exception {
        if (file != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(postService.saveListFile(file, request));
        }
        throw new NotFoundExceptionClass("No filename to upload");
    }
}
