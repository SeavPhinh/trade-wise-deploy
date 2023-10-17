package com.example.manageuserservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.manageuserservice.config.FileStorageProperties;
import com.example.manageuserservice.model.FileStorage;
import com.example.manageuserservice.model.UserInfo;
import com.example.manageuserservice.repository.FileRepository;
import com.example.manageuserservice.repository.UserInfoRepository;
import com.example.manageuserservice.request.FileRequest;
import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.response.FileResponse;
import com.example.manageuserservice.response.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final FileStorageProperties fileStorageProperties;
    private final FileRepository fileRepository;
    private final WebClient webClient;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, FileStorageProperties fileStorageProperties, FileRepository fileRepository, WebClient.Builder webClient) {
        this.userInfoRepository = userInfoRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.fileRepository = fileRepository;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
    }

    @Override
    public FileResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException {

        if (file != null && !isImageFile(file)) {
            throw new IllegalArgumentException(ValidationConfig.INVALID_FILE);
        }

        FileStorage obj = new FileStorage();
        obj.setFileName(file.getOriginalFilename().replaceAll("\\s+","") + UUID.randomUUID());
        obj.setFileType(file.getContentType());
        obj.setSize(file.getSize());
        obj.setFileUrl(String.valueOf(request.getRequestURL()).substring(0,22)+"images/"+obj.getFileName());

        String uploadPath = fileStorageProperties.getUploadPath();
        Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
        java.io.File directory = directoryPath.toFile();

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = file.getOriginalFilename();
        File dest = new File(directoryPath.toFile(), fileName);
        file.transferTo(dest);

        UserInfo preUserInfo = userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        preUserInfo.setProfileImage(obj.getFileName());
        userInfoRepository.save(preUserInfo);

        return fileRepository.save(new FileRequest(obj.getFileName(),obj.getFileUrl(),obj.getFileType(),obj.getSize()).toEntity()).toDto();

    }

    @Override
    public UserInfoResponse addUserDetail(UserInfoRequest request) {
        return userInfoRepository.save(request.toEntity(createdBy(UUID.fromString(currentUser())).getId())).toDto();
    }

    @Override
    public UserInfoResponse getUserInfoByOwnerId(UUID id) {
        return userInfoRepository.findByOwnerId(id).toDto();
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        return userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId()).toDto();
    }

    @Override
    public UserInfoResponse updateCurrentUserInfo(UserInfoRequest request) {
        UserInfo preUserInfo = userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        preUserInfo.setGender(request.getGender());
        preUserInfo.setDob(request.getDob());
        preUserInfo.setStreet(request.getStreet());
        preUserInfo.setCountry(request.getCountry());
        preUserInfo.setProvince(request.getProvince());
        preUserInfo.setPhoneNumber(request.getPhoneNumber());
        preUserInfo.setProfileImage(request.getProfileImage());
        return userInfoRepository.save(preUserInfo).toDto();
    }

    // Returning Token
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Decode to Get User Id
//            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
            DecodedJWT decodedJWT = JWT.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXckJaS3JoVDBIRXhhMU9FWUNJcUhOWjlSQkpyUVNRejZMVlQ1dEFEU1BnIn0.eyJleHAiOjE2OTc1MjE3ODksImlhdCI6MTY5NzUyMTQ4OSwianRpIjoiODAxMzk3OWUtMDU5Ni00OGUwLWJlYzEtYmJkOTY5ODBjYmIwIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjcwMzUzYTEzLWI0ZDQtNDFkNi05ZDJhLWFmNDIxOGQ4NzdlNSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiNGJlNDIxYjItYTA0MS00YjcyLWEwOTYtOWU4NTA0MjU2ZDBkIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjRiZTQyMWIyLWEwNDEtNGI3Mi1hMDk2LTllODUwNDI1NmQwZCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImZpcnN0bmFtZSBsYXN0bmFtZSIsInByZWZlcnJlZF91c2VybmFtZSI6ImJ1eWVyIiwiZ2l2ZW5fbmFtZSI6ImZpcnN0bmFtZSIsImZhbWlseV9uYW1lIjoibGFzdG5hbWUiLCJlbWFpbCI6ImJ1eWVyQGdtYWlsLmNvbSJ9.Gd0eRvPCN1uJ70ITIqCfZpdhOgQ53hqmmqQUs93skCBRBmwxRy407V_sMJ3Po998nLc7m7qfszO8x_2HXUEPq0t_hnHHrghHyY-cWTOKnlYV9iK7tAFpDr-vGfSAp0CD1Z9x4T8aLAtDM1zt3yLc7_HJrZwAwbQH95AxgEJAAGUWqBAuirMcGptn746u7XtTSsT4XcaseQBLUPhbxzr8EZIM656tPSSXxJC32OEEFXh6kz0-ubew7WqAHQaeGR4wVWKMVdjRtSwgKcdc2ASpzPIg8aljq5cW0UcOggLvtt4XmQQGqxa5BFSaOqVuEoQ66qNJL4VIGpdre-GO7tuM0g");
            return decodedJWT.getSubject();
        } else {
            return null;
        }
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

    // Validation Image
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            return contentType.equals("image/jpeg") ||
                    contentType.equals("image/png") ||
                    contentType.equals("image/tiff");
        }
        return false;
    }

}
