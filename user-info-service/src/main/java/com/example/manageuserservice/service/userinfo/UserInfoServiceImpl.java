package com.example.manageuserservice.service.userinfo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.manageuserservice.config.FileStorageProperties;
import com.example.manageuserservice.exception.NotFoundExceptionClass;
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
import java.util.UUID;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private final UserInfoRepository userInfoRepository;
    private final FileStorageProperties fileStorageProperties;
    private final FileRepository fileRepository;
    private final WebClient userWeb;

    public UserInfoServiceImpl(UserInfoRepository userInfoRepository, FileStorageProperties fileStorageProperties, FileRepository fileRepository, WebClient.Builder userWeb) {
        this.userInfoRepository = userInfoRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.fileRepository = fileRepository;
        this.userWeb = userWeb.baseUrl("http://localhost:8081/").build();
    }

    @Override
    public FileResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException {
        if (file != null && !isImageFile(file)) {
            throw new IllegalArgumentException(ValidationConfig.INVALID_FILE);
        }
        FileStorage obj = new FileStorage();
        obj.setFileName(UUID.randomUUID() + file.getOriginalFilename().replaceAll("\\s+",""));
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
    public UserInfoResponse addUserDetail(UserInfoRequest request) throws Exception {
        UserInfo user = userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(request.getGender() == null){
            throw new IllegalArgumentException(ValidationConfig.NULL_GENDER);
        }
        if(user != null){
            throw new IllegalArgumentException(ValidationConfig.FOUND_DETAIL);
        }
        validateFile(request.getProfileImage());
        return userInfoRepository.save(request.toEntity(isAccept(request.getPhoneNumber()),createdBy(UUID.fromString(currentUser())).getId())).toDto();
    }

    @Override
    public UserInfoResponse getUserInfoByOwnerId(UUID id) {
        return isNotExisting(id);
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        return isNotExisting(createdBy(UUID.fromString(currentUser())).getId());
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

    // User doesn't exist
    public UserInfoResponse isNotExisting(UUID id){
        UserInfo isFound = userInfoRepository.findByOwnerId(id);
        if(isFound != null){
            return userInfoRepository.findByOwnerId(id).toDto();
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
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
    public String currentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                // Decode to Get User Id
//            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
                DecodedJWT decodedJWT = JWT.decode("ekJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXckJaS3JoVDBIRXhhMU9FWUNJcUhOWjlSQkpyUVNRejZMVlQ1dEFEU1BnIn0.eyJleHAiOjE2OTc2OTQ4MzgsImlhdCI6MTY5NzY5NDUzOCwianRpIjoiZTU2MGY3YmUtNDhkOS00MzI4LWI5NTctMjE3ZDM2YjNlNjlkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjcwMzUzYTEzLWI0ZDQtNDFkNi05ZDJhLWFmNDIxOGQ4NzdlNSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiZGVhNjhhOWYtNjYyNy00MGI4LTkzNjctMGIwYjcyYmIzZjE5IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImRlYTY4YTlmLTY2MjctNDBiOC05MzY3LTBiMGI3MmJiM2YxOSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImZpcnN0bmFtZSBsYXN0bmFtZSIsInByZWZlcnJlZF91c2VybmFtZSI6ImJ1eWVyIiwiZ2l2ZW5fbmFtZSI6ImZpcnN0bmFtZSIsImZhbWlseV9uYW1lIjoibGFzdG5hbWUiLCJlbWFpbCI6ImJ1eWVyQGdtYWlsLmNvbSJ9.FS6KgdPqotE9rHKvyUv9L9FWZCHjKGXHDphsSZs1spsguAwX48hHzFsp-pLZh6odlwzWPRWYSA4Kw13ehY8hLALOTrBOJdzlo8hbC0bPsd5AREEjIpiVfWclOYxsYbqGfd-schEEeCxX_VwDj0EjA22Ovxjtcv07TrO7z1cvUw36zZI0ra3VuUwC1DgsIbEkboXDMVaPOP2m1JgGyjQbMGFCddTQCF18zj_sTXEpwAEpKfdHn-725wPV-X-BMa6EKqDOEgFlfIT2-yjYSJj1JXJH_kmd4gdFwOXvSiWUG_i32dhl-x8YuL2ikzzZLFe_xD6OG9B8_bVmU-s5byCawA");
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
}

