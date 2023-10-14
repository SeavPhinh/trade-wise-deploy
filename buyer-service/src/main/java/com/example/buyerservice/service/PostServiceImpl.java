package com.example.buyerservice.service;
import com.example.buyerservice.config.FileStorageProperties;
import com.example.buyerservice.model.FileStorage;
import com.example.buyerservice.model.Post;
import com.example.buyerservice.repository.PostRepository;
import com.example.buyerservice.request.FileRequest;
import com.example.buyerservice.request.PostRequest;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final FileStorageProperties fileStorageProperties;


    public PostServiceImpl(PostRepository postRepository, FileStorageProperties fileStorageProperties) {
        this.postRepository = postRepository;
        this.fileStorageProperties = fileStorageProperties;
    }

//    public AccessTokenResponse returnToken() {
//
//        Keycloak keycloak = KeycloakBuilder.builder()
//                .serverUrl("http://localhost:1234/auth")
//                .realm("go-selling-api")
//                .grantType(OAuth2Constants.PASSWORD)
//                .clientId("go-selling")
//                .clientSecret("mp3npE4H0LeAUn5LJWeFl7oQ55R6ypL0")
//                .username("receiver")
//                .password("123saK@")
//                .build();
//
//        AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
//        String accessToken = tokenResponse.getToken();
//
//        // Decode the access token to get user information
//        DecodedJWT jwt = JWT.decode(accessToken);
//        String userId = jwt.getSubject();
//
//        // Now, you can use the user ID as needed
//        System.out.println("User ID: " + userId);
//
//        return tokenResponse;
//    }



    @Override
    public Post createPost(PostRequest postRequest){

        Post createdPost = new Post();

        createdPost.setStatus(false);
        createdPost.setDescription(postRequest.getDescription());
        createdPost.setTitle(postRequest.getTitle());
        createdPost.setFile(postRequest.getFile().toString());
        createdPost.setSubCategoryId(postRequest.getSubCategoryId());
        createdPost.setUserId(UUID.fromString(currentUser()));
        createdPost.setCreatedDate(LocalDateTime.now());
        createdPost.setLastModified(LocalDateTime.now());

        postRepository.save(createdPost);

        return createdPost;
    }

    @Override
    public List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException {
        List<FileRequest> filesResponses = new ArrayList<>();

        for (MultipartFile file : files) {

            String uploadPath = fileStorageProperties.getUploadPath();
            Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
            java.io.File directory = directoryPath.toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = file.getOriginalFilename();
            File dest = new File(directoryPath.toFile(), fileName);
            file.transferTo(dest);

            FileStorage obj = new FileStorage();
            obj.setFileName(file.getOriginalFilename());
            obj.setFileType(file.getContentType());
            obj.setSize(file.getSize());
            obj.setFileUrl(String.valueOf(request.getRequestURL()).substring(0,22)+"images/"+obj.getFileName());
            filesResponses.add(new FileRequest(obj.getFileName(), obj.getFileUrl(),obj.getFileType(),obj.getSize()));
        }

        return filesResponses;
    }


    // Returning Token
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();;
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            // Decode to Get User Id
            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
            String userId = decodedJWT.getSubject();
            return userId;
        } else {
            return null;
        }
    }


}
