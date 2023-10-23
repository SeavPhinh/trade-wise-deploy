package com.example.postservice.service;


import com.example.commonservice.enumeration.Role;
import com.example.postservice.config.FileStorageProperties;
import com.example.postservice.exception.NotFoundExceptionClass;
import com.example.postservice.model.FileStorage;
import com.example.postservice.model.Post;
import com.example.postservice.repository.FileRepository;
import com.example.postservice.repository.PostRepository;
import com.example.postservice.request.FileRequest;
import com.example.postservice.request.PostRequest;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.postservice.response.PostResponse;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FileStorageProperties fileStorageProperties;
    private final WebClient webClient;
    private final FileRepository fileRepository;

    public PostServiceImpl(PostRepository postRepository, FileStorageProperties fileStorageProperties, WebClient.Builder webClient, FileRepository fileRepository) {
        this.postRepository = postRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
        this.fileRepository = fileRepository;
    }

    @Override
    public PostResponse createPost(PostRequest postRequest) {

        User tempUser= createdBy(UUID.fromString(currentUser()));
        //cut white spaces left and right
        String trimmedTitle= postRequest.getTitle().trim();
    String trimmedDescription= postRequest.getDescription().trim();

        String numberPattern = "^[+]?(?:\\d+\\.?\\d*|\\d*\\.\\d+|\\d+)$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(numberPattern);

        // Create a Matcher to match the input against the pattern
        Matcher matcher = pattern.matcher(postRequest.getBudgetFrom().toString());
        Matcher matcher2 = pattern.matcher(postRequest.getBudgetTo().toString());

        if(!(matcher.matches() && matcher2.matches())){
            throw new IllegalArgumentException("Opps, please input the valid budget");
        }


        if(tempUser.getRoles().contains(Role.BUYER) ){ // check if not role BUYER throw an exception
            postRequest.setTitle(trimmedTitle);
            postRequest.setDescription(trimmedDescription);

            return postRepository.save(postRequest.toEntity(UUID.fromString(currentUser())))
                    .toDto(postRequest.getFile(),createdBy(UUID.fromString(currentUser())));
        }
        else{
        throw new IllegalArgumentException("Opps, only BUYER can create post.");
        }
    }

    @Override
    public List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request, UUID postId) throws IOException {
        User tempUser = createdBy(UUID.fromString(currentUser()));

        if(!tempUser.getRoles().contains(Role.BUYER) ){
            throw new IllegalArgumentException("Opps, only BUYER can create post.");
        }
        Post post = postRepository.findPostById(postId);
        Post drafted = postRepository.findDraftedPostById(postId);

        if(post==null && drafted==null){
            throw new NotFoundExceptionClass("Opps, this id doesn't exist.");
        }


        List<FileRequest> filesResponses = new ArrayList<>();
        List<String> listFiles = new ArrayList<>();
        for (MultipartFile file : files) {

                   if(!file.getContentType().equals("image/png")
                           || file.getContentType().equals("image/tiff")
                           || file.getContentType().equals("image/jpeg")
                           || file.getContentType().equals("video/mp4")
                           || file.getContentType().equals("video/avi")
                           || file.getContentType().equals("video/quicktime")
            ) {
                throw new IllegalArgumentException("Opps. please input the valid images or videos.");
            }

            String uploadPath = fileStorageProperties.getUploadPath();
            Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
            java.io.File directory = directoryPath.toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = UUID.randomUUID() + file.getOriginalFilename().replaceAll("\\s+","");
            File dest = new File(directoryPath.toFile(), fileName);
            file.transferTo(dest);
            FileStorage obj = new FileStorage();
            obj.setFileName(fileName);
            obj.setFileType(file.getContentType());
            obj.setFilePath(directoryPath + "\\" + fileName);
            obj.setSize(file.getSize());
            obj.setFileUrl(String.valueOf(request.getRequestURL()).substring(0,22)+"images/"+fileName);
            listFiles.add(obj.getFileName());
            filesResponses.add(new FileRequest(fileName, obj.getFileUrl(),obj.getFileType(),obj.getFilePath(),obj.getSize()));
            fileRepository.save(obj);
        }

        Post prePost = postRepository.findById(postId).orElseThrow();
        prePost.setFile(listFiles.toString());
        postRepository.save(prePost);
        return filesResponses;
    }

    @Override
    public List<PostResponse> getAllPost() {
        List<PostResponse> posts= postRepository.findAllPosts().stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).toList();
        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }

        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, post is currently empty.");
        }
        return posts;
    }

    @Override
    public PostResponse getPostById(UUID id) {
        Post post = postRepository.findPostById(id);
        if(post==null){
            throw new NotFoundExceptionClass("Opps, this post cannot be found.");
        }else
            return postRepository.findPostById(id).toDto(getFiles(postRepository.findPostById(id)),createdBy(UUID.fromString(currentUser())));

    }

    @Override
    public PostResponse deletePostById(UUID id) {
        Optional<Post> post= postRepository.findById(id);
        if(post.isEmpty()){
            throw new NotFoundExceptionClass("Opps, this post cannot be found.");
        }

        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.BUYER) ){
            throw new IllegalArgumentException("Opps, only BUYER can use this endpoint.");
        }
        // Create new object to store before delete
        PostResponse response = getPostById(id);
        postRepository.deleteById(id);

        return response;
    }

    @Override
    public PostResponse updatePostById(UUID id, PostRequest postRequest) {
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.BUYER) ){
            throw new IllegalArgumentException("Opps, only BUYER can use this endpoint.");
        }

        Optional<Post> post= postRepository.findById(id);
        if(post.isEmpty()){
            throw new NotFoundExceptionClass("Opps, this id cannot be found.");
        }


        String trimmedTitles= postRequest.getTitle().trim();
        String trimmedDescription= postRequest.getDescription().trim();

        String numberPattern = "^[+]?(?:\\d+\\.?\\d*|\\d*\\.\\d+|\\d+)$";

        // Compile the pattern
        Pattern pattern = Pattern.compile(numberPattern);

        // Create a Matcher to match the input against the pattern
        Matcher matcher = pattern.matcher(postRequest.getBudgetFrom().toString());
        Matcher matcher2 = pattern.matcher(postRequest.getBudgetTo().toString());

        if(!(matcher.matches() && matcher2.matches())){
            throw new IllegalArgumentException("Opps, please input the valid budget.");
        }




        Post preData = postRepository.findById(id).orElseThrow();

        // Update Previous Data
        preData.setTitle(trimmedTitles);
        preData.setFile(postRequest.getFile().toString());
        preData.setDescription(trimmedDescription);
        preData.setBudgetFrom(postRequest.getBudgetFrom());
        preData.setBudgetTo(postRequest.getBudgetTo());
        preData.setStatus(postRequest.getStatus());
        preData.setLastModified(LocalDateTime.now());
        preData.setSubCategory(postRequest.getSubCategory());

        return postRepository.save(preData).toDto(getFiles(preData),createdBy(UUID.fromString(currentUser())));

    }

    @Override
    public List<PostResponse> getAllDraftPosts() {
        List<PostResponse> draftedposts = postRepository.getAllDraftPosts().stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString( currentUser())))).toList();
       if(draftedposts.isEmpty()){
           throw new NotFoundExceptionClass("Opps, the drafted post is currently empty.");
       }
        return draftedposts;
    }

    @Override
    public PostResponse getDraftedPostById(UUID id) {
        Post post = postRepository.findDraftedPostById(id);
        if(post==null){
            throw new NotFoundExceptionClass("Opps, this drafted post cannot be found.");
        }
        else return postRepository.findDraftedPostById(id).toDto(getFiles(postRepository.findDraftedPostById(id)),createdBy(UUID.fromString(currentUser())));
    }

    @Override
    public byte[] getImageByName(String name) throws IOException {

         FileStorage imageData=  fileRepository.findByName(name);
         if(imageData==null){
             throw new NotFoundExceptionClass("Opps, image cannot be found.");
         }

        return Files.readAllBytes(new File(imageData.getFilePath()).toPath());
    }

    @Override
    public List<PostResponse> findByBudgetFromAndBudgetTo(Float budgetFrom, Float budgetTo) {
        String numberPattern = "^[+]?(?:\\d+\\.?\\d*|\\d*\\.\\d+|\\d+)$";
        // Compile the pattern
        Pattern pattern = Pattern.compile(numberPattern);

        // Create a Matcher to match the input against the pattern
        Matcher matcher1 = pattern.matcher(budgetFrom.toString());
        Matcher matcher2 = pattern.matcher(budgetTo.toString());

        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }

        if(!(matcher1.matches() && matcher2.matches())){
            throw new IllegalArgumentException("Opps, please input the valid number of budget.");
        }
        if(budgetFrom >= budgetTo ){
            throw new IllegalArgumentException("Opps, budget-from has to be smaller than budget-to");
        }

        List<Post> posts = postRepository.findByBudgetFromAndBudgetTo(budgetFrom,budgetTo);
        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Posts not found");
        }
        return posts.stream().map(post-> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getAllPostSortedByNewest() {
        List<Post> posts =postRepository.findAllSortedByNewest();
        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }
        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, post is currently empty.");
        }
        return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getAllPostSortedByOldest() {
        List<Post> posts =postRepository.findAllSortedByOldest();
        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }
        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, post is currently empty.");
        }
        return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getAllPostSortedByAZ() {
        List<Post> posts =postRepository.findAllSortedByAZ();
        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }
        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, post is currently empty.");
        }
        return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getAllPostSortedByZA() {
        List<Post> posts =postRepository.findAllSortedByZA();
        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }
        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, post is currently empty.");
        }
        return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }



    @Override
    public List<PostResponse> getAllPostSortedBySubCategory(String subCategory) {
        List<Post> posts= postRepository.getAllPostSortedBySubCategory(subCategory);
        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }
        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, post is currently empty.");
        }
        return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> searchPostBySubCategory(String subCategory) {
        List<Post> posts = postRepository.searchPostBySubCategory(subCategory);
        //check if not role SELLER throw an exception
        User tempUser = createdBy(UUID.fromString(currentUser()));
        if(!tempUser.getRoles().contains(Role.SELLER) ){
            throw new IllegalArgumentException("Opps, only SELLER can use this endpoint.");
        }

        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, post cannot be found.");
        }
        return posts.stream().map(post -> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }

    @Override
    public List<PostResponse> getPostsForCurrentUser() {
        List<Post> posts =postRepository.findAllPostForCurrentUser(UUID.fromString(currentUser()));
        if(posts.isEmpty()){
            throw new NotFoundExceptionClass("Opps, you haven't posted anything yet.");
        }
        return posts.stream().map(post-> post.toDto(getFiles(post),createdBy(UUID.fromString(currentUser())))).collect(Collectors.toList());
    }


    // Returning Token
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();;
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Decode to Get User Id
//            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
            DecodedJWT decodedJWT = JWT.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4c0JZb3JLbG5qSnl1QjBMc3dPM1N2aU1aMTFOQktFLVV6LU54a0hZWUVjIn0.eyJleHAiOjE2OTc2MDA2MzcsImlhdCI6MTY5NzYwMDMzNywianRpIjoiZTBhY2JmYzEtOGQ0ZS00OTlmLWFhZmMtZjVjODljYTNkYjdhIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImM2M2ZkYWY4LTMxOGMtNDVlNC1hMjEzLTQ3ZGE1ZGU4Y2JlYiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiMWUzY2U1NmUtOTk1MS00MzYyLTg3YzgtOTY1ODg4ZGIwZmEwIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjFlM2NlNTZlLTk5NTEtNDM2Mi04N2M4LTk2NTg4OGRiMGZhMCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImZpcnN0IGxhc3QiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtYW5uIiwiZ2l2ZW5fbmFtZSI6ImZpcnN0IiwiZmFtaWx5X25hbWUiOiJsYXN0IiwiZW1haWwiOiJtYW5uQGdtYWlsLmNvbSJ9.RYkSLdYmjO0Ji8lVNQsv5YoTZWOnoYzVUkc8KNYmPabAORQy3Eo4eGa__F9WFhQsfn_-FQhUm1gopSxelTDW0EpJzf45yV4chbkLbke8QC66PUgLuJyEnEYmdIVIRmJXz2PZ9SmTPUFoo7P3dTDhlJdPmMjpqKbD8g7sW3af_Frv4l5L3vTQJ6hl9o5eX_6jNxLwhzpr9S8G0qqMorzmrs0gLnOzxb3j_Z-_CIPa1UPmlfiPJpmYpYR1ZIVGgeLX2ZoMn-NCwNNVRQAJl1IIZKe6oQZG-PmKlMu4tf8Hl0mMuBbF57xiIHxzccDyycRflKbY4z6b692G2l4NmN_f2A");
            return decodedJWT.getSubject();
        } else {
            return null;
        }
    }

    // Separate string files -> List
    private List<String> getFiles(Post post) {
        return Arrays.asList(post.getFile().replaceAll("\\[|\\]", "").split(", "));
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


}
