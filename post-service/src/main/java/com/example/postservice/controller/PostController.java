package com.example.postservice.controller;
import com.example.postservice.enums.Filter;
import com.example.postservice.response.PostResponse;
import com.example.postservice.service.PostService;
import com.example.commonservice.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@CrossOrigin
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
    public ResponseEntity<ApiResponse<List<PostResponse>>> filterPostByBudget(@RequestParam(defaultValue = "0.0") Float budgetFrom,
                                                                              @RequestParam(defaultValue = "1.0") Float budgetTo) {
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by budget fetched successfully",
                postService.findByBudgetFromAndBudgetTo(budgetFrom,budgetTo),
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

    @GetMapping("/alphabet")
    @Operation(summary = "get all post sorted by alphabet")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPostSortedByAlphabet(@RequestParam(defaultValue = "AZ")Filter filter){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts filtered by alphabet fetched successfully",
                postService.getAllPostSortedByAlphabet(filter),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/sub-category/list")
    @Operation(summary = "search all post by based on sub-category")
    public ResponseEntity<ApiResponse<List<PostResponse>>> searchPostBySubCategory(@RequestParam List<String> filter){
        return new ResponseEntity<>(new ApiResponse<>(
                "posts searched based on sub-category fetched successfully",
                postService.searchPostBySubCategory(filter),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/sub-category/random")
    @Operation(summary = "random three post based on sub-category")
    public ResponseEntity<ApiResponse<List<PostResponse>>> randomPostBySubCategory(@RequestParam String subCategory){
        return new ResponseEntity<>(new ApiResponse<>(
                "random three post by based on sub-category fetched successfully",
                postService.randomPostBySubCategory(subCategory),
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

    @GetMapping("/sub-category/range")
    @Operation(summary = "fetch all post based on sub-category and range budget")
    public ResponseEntity<ApiResponse<List<PostResponse>>> rangeBudget(@RequestParam List<String> subCategory,
                                                                       @RequestParam Float budgetFrom,
                                                                       @RequestParam Float budgetTo){
        return new ResponseEntity<>(new ApiResponse<>(
                "Fetch by SubCategory and Range Budget successfully.",
                postService.filterRequest(subCategory,budgetFrom,budgetTo),
                HttpStatus.OK
        ),HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    @Operation(summary = "fetch all post by user id")
    public ResponseEntity<ApiResponse<List<PostResponse>>> fetchByUserId(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "Fetch all post by user id successfully.",
                postService.getAllPostByUserId(id),
                HttpStatus.OK
        ),HttpStatus.OK);
    }


}
