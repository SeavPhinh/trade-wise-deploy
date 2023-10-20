package com.example.postservice.controller;

import com.example.postservice.model.Post;
import com.example.postservice.request.PostRequest;
import com.example.postservice.response.PostResponse;
import com.example.postservice.service.PostService;
import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@Tag(name = "Post")
@SecurityRequirement(name = "oAuth2")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/posts")
    @Operation(summary = "user created post")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                "User created post successfully",
                postService.createPost(postRequest),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("/posts")
    @Operation(summary = "fetch all posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPost() {
        return new ResponseEntity<>(new ApiResponse<>(
                "Posts fetched successfully",
                postService.getAllPost(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "fetch post by id")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable UUID id) {
        return new ResponseEntity<>(new ApiResponse<>(
                "User fetched by id successfully",
                postService.getPostById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @DeleteMapping("/posts/{id}")
    @Operation(summary = "delete post by id")
    public ResponseEntity<ApiResponse<PostResponse>> deletePostById(@PathVariable UUID id) {

        return new ResponseEntity<>(new ApiResponse<>(
                "post delete by id successfully",
                postService.deletePostById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/posts/{id}")
    @Operation(summary = "update post and drafted post by id")
    public ResponseEntity<ApiResponse<PostResponse>> updatePostById(@PathVariable UUID id,
                                                                    @Valid @RequestBody PostRequest request) {
        return new ResponseEntity<>(new ApiResponse<>(
                " Updated post by id successfully",
                postService.updatePostById(id, request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }


    @PutMapping(value = "/upload/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload multiple file")
    public ResponseEntity<?> saveMultiFile(@RequestParam(required = false) List<MultipartFile> files,
                                           HttpServletRequest request,
                                           @PathVariable UUID postId) throws IOException {
        if (files != null) {
            return ResponseEntity.status(200).body(postService.saveListFile(files, request, postId));
        }
        throw new NotFoundExceptionClass("No filename to upload");
    }


    @GetMapping("/posts/drafted")
    @Operation(summary = "fetch all drafted posts ")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllDraftPosts() {
        return new ResponseEntity<>(new ApiResponse<>(
                "Drafted posts fetched successfully",
                postService.getAllDraftPosts(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/drafted/{id}")
    @Operation(summary = "fetch drafted post by id")
    public ResponseEntity<ApiResponse<PostResponse>> getDraftedPostById(@PathVariable UUID id) {
        return new ResponseEntity<>(new ApiResponse<>(
                "Drafted post fetched by id successfully",
                postService.getDraftedPostById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/image/{fileName}")
    @Operation(summary = "get all image by name")
    public ResponseEntity<?> getImageByName(@PathVariable("fileName") String name) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(postService.getImageByName(name));
    }

    @GetMapping("/posts/budget")
    @Operation(summary = "get all posts by budget (get all posts as long as the buyer can buy)")
    public ResponseEntity<ApiResponse<List<PostResponse>>> filterPostByBudget(@RequestParam Float budgetFrom,@RequestParam Float budgetTo) {
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by budget fetched successfully",
                postService.findByBudgetFromAndBudgetTo(budgetFrom,budgetTo),
                HttpStatus.OK
                ),HttpStatus.OK);
    }


    @GetMapping("/posts/newest")
    @Operation(summary = "get all post sorted buy newest")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByNewest(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by newest fetched successfully",
                postService.getAllPostSortedByNewest(),
                HttpStatus.OK
                ),HttpStatus.OK);
    }

    @GetMapping("/posts/oldest")
    @Operation(summary = "get all post sorted buy oldest")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByOldest(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by oldest fetched successfully",
                postService.getAllPostSortedByOldest(),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/posts/a-z")
    @Operation(summary = "get all post sorted buy a-z")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByAZ(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by a-z fetched successfully",
                postService.getAllPostSortedByAZ(),
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @GetMapping("/posts/z-a")
    @Operation(summary = "get all post sorted buy z-a")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByZA(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by z-a fetched successfully",
                postService.getAllPostSortedByZA(),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

}
