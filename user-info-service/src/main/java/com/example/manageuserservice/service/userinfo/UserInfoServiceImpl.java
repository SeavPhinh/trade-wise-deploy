package com.example.manageuserservice.service.userinfo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.manageuserservice.config.FileStorageProperties;
import com.example.manageuserservice.exception.NotFoundExceptionClass;
import com.example.manageuserservice.model.UserInfo;
import com.example.manageuserservice.repository.UserInfoRepository;
import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.response.UserInfoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Objects;
import java.util.UUID;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final FileStorageProperties fileStorageProperties;
    private final Keycloak keycloak;
    private final WebClient userWeb;

    @Value("${keycloak.realm}")
    private String realm;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, FileStorageProperties fileStorageProperties, Keycloak keycloak, WebClient.Builder userWeb) {
        this.userInfoRepository = userInfoRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.keycloak = keycloak;
        this.userWeb = userWeb.baseUrl("http://localhost:8081/").build();
    }

    @Override
    public UserInfoResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file != null && !isImageFile(file)) {
            throw new IllegalArgumentException(ValidationConfig.INVALID_FILE);
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
        UserInfo preUserInfo = userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        preUserInfo.setProfileImage(fileName);
        userInfoRepository.save(preUserInfo);
        return preUserInfo.toDto(createdBy(preUserInfo.getUserId()));
    }

    @Override
    public UserInfoResponse addUserDetail(UserInfoRequest request) throws Exception {
        UserInfo user = userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(request.getGender() == null){
            throw new IllegalArgumentException(ValidationConfig.NULL_GENDER);
        }
        if(user != null){
            throw new IllegalArgumentException(ValidationConfig.FOUND_DETAIL);
        }
        validateFile(request.getProfileImage());
        return userInfoRepository.save(request.toEntity(isAccept(request.getPhoneNumber()),createdBy(UUID.fromString(currentUser())).getId())).toDto(createdBy(createdBy(UUID.fromString(currentUser())).getId()));
    }

    @Override
    public UserInfoResponse getUserInfoByUserId(UUID id) {
        return isNotExisting(id);
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        return isNotExisting(createdBy(UUID.fromString(currentUser())).getId());
    }

    @Override
    public UserInfoResponse updateCurrentUserInfo(UserInfoRequest request) {
        UserInfo preUserInfo = userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId());

        // Update Firstname & Lastname of user in keycloak
        UserRepresentation updatedUser = new UserRepresentation();
        resource(UUID.fromString(currentUser()));
        updatedUser.setFirstName(request.getFirstname().replaceAll("\\s+",""));
        updatedUser.setLastName(request.getLastname().replaceAll("\\s+",""));
        resource(UUID.fromString(currentUser())).update(updatedUser);

        // Update In User-info database
        preUserInfo.setGender(request.getGender());
        preUserInfo.setDob(request.getDob());
        preUserInfo.setPhoneNumber(request.getPhoneNumber());
        preUserInfo.setProfileImage(request.getProfileImage());
        return userInfoRepository.save(preUserInfo).toDto(createdBy(preUserInfo.getUserId()));
    }

    @Override
    public ByteArrayResource getImage(String fileName) throws IOException {
        String filePath = "user-info-service/src/main/resources/storage/" + fileName;
        Path path = Paths.get(filePath);

        if(!Files.exists(path)){
            throw new NotFoundExceptionClass(ValidationConfig.FILE_NOTFOUND);
        }
        String uploadPath = fileStorageProperties.getUploadPath();
        Path paths = Paths.get(uploadPath + fileName);
        return new ByteArrayResource(Files.readAllBytes(paths));
    }

    // User doesn't exist
    public UserInfoResponse isNotExisting(UUID id){
        UserInfo isFound = userInfoRepository.findByOwnerId(id);
        if(isFound != null){
            return userInfoRepository.findByOwnerId(id).toDto(createdBy(isFound.getUserId()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER_INFO);
    }

    // Phone Number Validating
    public String isAccept(String number){
        try {
            Integer.valueOf(number);
            if (number.length() < ValidationConfig.MIN_PH || number.length() > ValidationConfig.MAX_PH) {
                throw new IllegalArgumentException(ValidationConfig.MIN_MAX_PH);
            }else if (number.startsWith("00")) {
                throw new IllegalArgumentException(ValidationConfig.INVALID_PH);
            } else {
                return number.replaceFirst("^0*", "");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ValidationConfig.INVALID_PH);
        }
    }

    // Returning Token
    public String currentUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                // Decode to Get User Id
//            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
                DecodedJWT decodedJWT = JWT.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXckJaS3JoVDBIRXhhMU9FWUNJcUhOWjlSQkpyUVNRejZMVlQ1dEFEU1BnIn0.eyJleHAiOjE2OTc5ODYxODcsImlhdCI6MTY5Nzk4NTg4NywianRpIjoiOTc0MmYzNWYtYWE1My00MWQ3LTk4MjAtZDgzNjU1YjVhM2RmIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImFkZDc4MmM2LTFkYzktNGJjYi1hNjRkLWU2OTFiOTM4ODczNCIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiMDhjOGI5YTMtMzk2Yi00M2FmLTkxZGItNDI2YzA0MzYyNzQ5IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjA4YzhiOWEzLTM5NmItNDNhZi05MWRiLTQyNmMwNDM2Mjc0OSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6IlJpdGh5c2FrIFJlbiIsInByZWZlcnJlZF91c2VybmFtZSI6InJpdGh5c2FrIiwiZ2l2ZW5fbmFtZSI6IlJpdGh5c2FrIiwiZmFtaWx5X25hbWUiOiJSZW4iLCJlbWFpbCI6InJpdGh5c2FrcmVuQGdtYWlsLmNvbSJ9.egQpht_xDyLb-Qh08rsC-DqkiHiYvF_IRu_HeLLehOHYNtWQH9RdTCiHeh3iNgeJc_BhkrxK_X2C14-QVWrWjBPxqJ9Dl4B2oMcEVEXsJBr3uwU4mscnJScpDFACA9ubvLE6rvI-qp1-vZ1sOMYyucDpv5nNnFYyFuwWhR8312TfzTamld7tMmfKqkLgmW8XbkZp1FbOCowLBkrqApqlX29BqNG8045FImzNHp-BPe8AgcHS8Wqxf_R37BvKlO3fSUAKPOOBPnQ6G_yELayBmDs1pDvMg9jRX1vGkN-Aw65MaaCKdUPgTzQg43QMAZK5evr-_NHKafL5Pgs0nWoweQ");
                return decodedJWT.getSubject();
            }
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    // Return User
    public User createdBy(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        return covertSpecificClass.convertValue(Objects.requireNonNull(userWeb
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

    // Validation String image
    public static void validateFile(String fileName) throws Exception {
        String[] validExtensions = {".jpg", ".jpeg", ".png", ".tiff"};
        boolean isValidExtension = false;
        for (String extension : validExtensions) {
            if (fileName.toLowerCase().endsWith(extension)) {
                isValidExtension = true;
                break;
            }
        }
        if (!isValidExtension) {
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_FILE);
        }
    }

    // Returning UserResource by id
    public UserResource resource(UUID id){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getId().equalsIgnoreCase(String.valueOf(id))){
                return keycloak.realm(realm).users().get(String.valueOf(id));
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }


}

