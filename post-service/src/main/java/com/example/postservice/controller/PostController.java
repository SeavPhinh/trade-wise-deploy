package com.example.postservice.controller;
import com.example.postservice.request.RangeBudget;
import com.example.postservice.response.PostResponse;
import com.example.postservice.service.PostService;
import com.example.commonservice.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/posts")
@Tag(name = "Post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("")
    @Operation(summary = "fetched all BUYER's posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPost() {
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched all BUYER's post successfully",
                postService.getAllPost(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "fetched BUYER's post by id (not draft post)")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable UUID id) {
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched BUYER's post by id successfully",
                postService.getPostById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }


    @GetMapping("/drafted")
    @Operation(summary = "fetch all Buyer's drafted posts")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllDraftPosts() {
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched all BUYER's drafted posts successfully",
                postService.getAllDraftPosts(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/drafted/{id}")
    @Operation(summary = "fetch buyer's drafted post by id")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<PostResponse>> getDraftedPostById(@PathVariable UUID id) {
        return new ResponseEntity<>(new ApiResponse<>(
                "fetched BUYER's drafted posts by id successfully",
                postService.getDraftedPostById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/image/{fileName}")
    @Operation(summary = "get all image by name")
    public ResponseEntity<?> getImageByName(@PathVariable("fileName") String name) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(postService.getImage(name));
    }

    @GetMapping("/budget")
    @Operation(summary = "get all posts by budget (get all posts as long as the buyer can buy)")
    public ResponseEntity<ApiResponse<List<PostResponse>>> filterPostByBudget(@Valid @RequestBody RangeBudget budget) {
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by budget fetched successfully",
                postService.findByBudgetFromAndBudgetTo(budget),
                HttpStatus.OK
        ),HttpStatus.OK);
    }


    @GetMapping("/newest")
    @Operation(summary = "get all post sorted buy newest")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByNewest(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by newest fetched successfully",
                postService.getAllPostSortedByNewest(),
                HttpStatus.OK
                ),HttpStatus.OK);
    }

    @GetMapping("/oldest")
    @Operation(summary = "get all post sorted buy oldest")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByOldest(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by oldest fetched successfully",
                postService.getAllPostSortedByOldest(),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/a-z")
    @Operation(summary = "get all post sorted buy a-z")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByAZ(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by a-z fetched successfully",
                postService.getAllPostSortedByAZ(),
                HttpStatus.OK
        ),HttpStatus.OK);
    }
    @GetMapping("/z-a")
    @Operation(summary = "get all post sorted buy z-a")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByZA(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by z-a fetched successfully",
                postService.getAllPostSortedByZA(),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/sub-category")
    @Operation(summary = "get all post sorted sub-category")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedBySubCategory(@RequestParam String subCategory){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by sub-category fetched successfully",
                postService.getAllPostSortedBySubCategory(subCategory),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/sub-category/search")
    @Operation(summary = "search all post by sub-category")
    public ResponseEntity<ApiResponse<List<PostResponse>>> searchPostBySubCategory(@RequestParam String subCategory){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts searched by sub-category fetched successfully",
                postService.searchPostBySubCategory(subCategory),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/current")
    @Operation(summary = "get all post for current user")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsForCurrentUser(){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts for current user fetched successfully",
                postService.getPostsForCurrentUser(),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

}
