package com.example.postservice.service;

import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.response.CategorySubCategoryResponse;
import com.example.postservice.config.FileStorageProperties;
import com.example.postservice.exception.NotFoundExceptionClass;
import com.example.postservice.model.Post;
import com.example.postservice.repository.PostRepository;
import com.example.postservice.request.PostRequest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.postservice.request.RangeBudget;
import com.example.postservice.response.PostResponse;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FileStorageProperties fileStorageProperties;
    private final WebClient webClient;

    public PostServiceImpl(PostRepository postRepository, FileStorageProperties fileStorageProperties, WebClient.Builder webClient) {
        this.postRepository = postRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.webClient = webClient.baseUrl("http://192.168.154.1:8080/").build();
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {
        isLegal(UUID.fromString(currentUser()));
        if(!postRequest.getSubCategory().equalsIgnoreCase(categoriesList(postRequest.getSubCategory()))){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
        if(postRequest.getBudgetFrom() > postRequest.getBudgetTo()){
            throw new IllegalArgumentException(ValidationConfig.CANNOT_SMALLER);
        }
        return postRepository.save(postRequest.toEntity(UUID.fromString(currentUser()))).toDto(getFiles(postRequest.toEntity(UUID.fromString(currentUser()))),createdBy(UUID.fromString(currentUser())));
    }

    @Override
    public PostResponse saveListFile(List<MultipartFile> files, HttpServletRequest request, UUID postId) throws IOException {
        isLegal(UUID.fromString(currentUser()));
        Post post = postRepository.findPostById(postId);
        List<String> listFiles = new ArrayList<>();
        if(post != null){
            for (MultipartFile file : files) {
                String uploadPath = fileStorageProperties.getUploadPath();
                Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
                java.io.File directory = directoryPath.toFile();
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = UUID.randomUUID() + file.getOriginalFilename().replaceAll("\\s+","");
                File dest = new File(directoryPath.toFile(), fileName);
                file.transferTo(dest);
                listFiles.add(fileName);
                post.setFile(listFiles.toString());
                return postRepository.save(post).toDto(listFiles, createdBy(UUID.fromString(currentUser())));
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getAllPost() {
        List<PostResponse> posts = postRepository.findAllPosts().stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).toList();
        if(!posts.isEmpty()){
            return posts;
        }
        throw new NotFoundExceptionClass(ValidationConfig.POST_NOT_CONTAIN);
    }

    @Override
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findPostById(id);
        if(post != null){
            return postRepository.findPostById(id).toDto(getFiles(postRepository.findPostById(id)),createdBy(UUID.fromString(currentUser())));
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public String deletePostById(UUID id) {
        isLegal(UUID.fromString(currentUser()));
        Optional<Post> post= postRepository.findById(id);
        if(post.isPresent()){
            // Create new object to store before delete
            PostResponse response = getPostById(id);
            if(response.getCreatedBy().getId().toString().equalsIgnoreCase(currentUser())){
                postRepository.deleteById(id);
                return "Post delete successfully";
            }
            throw new IllegalArgumentException(ValidationConfig.NOT_OWNER_POST);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public PostResponse updatePostById(UUID id, PostRequest postRequest) {
        isLegal(UUID.fromString(currentUser()));
        Optional<Post> pre = postRepository.findById(id);
        if(pre.isPresent()){
            Post preData = pre.get();
            if(currentUser().equalsIgnoreCase(preData.getUserId().toString())){
                if(postRequest.getBudgetFrom() > postRequest.getBudgetTo()){
                    throw new IllegalArgumentException(ValidationConfig.CANNOT_SMALLER);
                }
                // Update Previous Data
                preData.setTitle(postRequest.getTitle());
                preData.setFile(postRequest.getFile().toString());
                preData.setDescription(postRequest.getDescription());
                preData.setBudgetFrom(postRequest.getBudgetFrom());
                preData.setBudgetTo(postRequest.getBudgetTo());
                preData.setStatus(postRequest.getStatus());
                preData.setLastModified(LocalDateTime.now());
                preData.setSubCategory(postRequest.getSubCategory());
                return postRepository.save(preData).toDto(getFiles(preData),createdBy(UUID.fromString(currentUser())));
            }
            throw new NotFoundExceptionClass(ValidationConfig.NOT_OWNER_POST);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getAllDraftPosts() {
        isLegal(UUID.fromString(currentUser()));
        List<PostResponse> draftList = postRepository.getAllDraftPosts().stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString( currentUser())))).toList();
        if(!draftList.isEmpty()){
            return draftList;
        }
        throw new NotFoundExceptionClass(ValidationConfig.UR_POST_LIST_NOT_CONTAIN);
    }

    @Override
    public PostResponse getDraftedPostById(UUID id) {
        isLegal(UUID.fromString(currentUser()));
        Post post = postRepository.findDraftedPostById(id);
        if(post != null){
            return postRepository.findDraftedPostById(id).toDto(getFiles(postRepository.findDraftedPostById(id)),createdBy(UUID.fromString(currentUser())));
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public ByteArrayResource getImage(String fileName) throws IOException {
        String filePath = "post-service/src/main/resources/storage/" + fileName;
        Path path = Paths.get(filePath);
        if(!Files.exists(path)){
            throw new NotFoundExceptionClass(ValidationConfig.FILE_NOTFOUND);
        }
        String uploadPath = fileStorageProperties.getUploadPath();
        Path paths = Paths.get(uploadPath + fileName);
        return new ByteArrayResource(Files.readAllBytes(paths));
    }

    @Override
    public List<PostResponse> findByBudgetFromAndBudgetTo(RangeBudget budget) {
        if(budget.getBudgetForm() > budget.getBudgetTo()){
            throw new IllegalArgumentException(ValidationConfig.CANNOT_SMALLER);
        }
        List<Post> posts = postRepository.findByBudgetFromAndBudgetTo(budget.getBudgetForm(), budget.getBudgetTo());
        if(!posts.isEmpty()){
            return posts.stream().map(post-> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getAllPostSortedByNewest() {
        List<Post> posts = postRepository.findAllSortedByNewest();
        if(!posts.isEmpty()){
            return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getAllPostSortedByOldest() {
        List<Post> posts = postRepository.findAllSortedByOldest();
        if(!posts.isEmpty()){
            return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getAllPostSortedByAZ() {
        List<Post> posts = postRepository.findAllSortedByAZ();
        if(!posts.isEmpty()){
            return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getAllPostSortedByZA() {
        List<Post> posts = postRepository.findAllSortedByZA();
        if(!posts.isEmpty()){
            return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getAllPostSortedBySubCategory(String subCategory) {
        List<Post> posts = postRepository.getAllPostSortedBySubCategory(subCategory);
        if(!posts.isEmpty()){
            return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> searchPostBySubCategory(String subCategory) {
        List<Post> posts = postRepository.searchPostBySubCategory(subCategory);
        if(!posts.isEmpty()){
            return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    @Override
    public List<PostResponse> getPostsForCurrentUser() {
        List<Post> posts = postRepository.findAllPostForCurrentUser(UUID.fromString(currentUser()));
        if(!posts.isEmpty()){
            return posts.stream().map(post-> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    // Returning Token
    public String currentUser() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                // Decode to Get User Id
                DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
                return decodedJWT.getSubject();
            }
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    // Separate string files -> List
    private List<String> getFiles(Post post) {
        return Arrays.asList(post.getFile().replaceAll(ValidationConfig.REGEX_ROLES, "").split(", "));
    }

    // Return User
    public User createdBy(UUID id){

        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());

        return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                .get()
                .uri("api/v1/users/{id}", id)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block()).getPayload(), User.class);
    }

    // Validation legal Role
    public void isLegal(UUID id){
        if(!createdBy(id).getLoggedAs().equalsIgnoreCase(String.valueOf(Role.BUYER))){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_PROCESS);
        }
    }

    // Converting Category list as UUID to List<String>
    public String categoriesList(String categories) {
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        try {
            CategorySubCategoryResponse subName = covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("api/v1/sub-categories")
                            .queryParam("name", categories.toUpperCase())
                            .build())
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), CategorySubCategoryResponse.class);
            return subName.getSubCategory().getName();
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
    }


}
