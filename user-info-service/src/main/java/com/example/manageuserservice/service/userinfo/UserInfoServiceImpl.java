package com.example.manageuserservice.service.userinfo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.FileResponse;
import com.example.manageuserservice.config.FileStorageProperties;
import com.example.manageuserservice.exception.NotFoundExceptionClass;
import com.example.manageuserservice.model.UserInfo;
import com.example.manageuserservice.repository.UserInfoRepository;
import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.request.UserInfoRequestUpdate;
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
        this.userWeb = userWeb.baseUrl("http://192.168.154.1:8080/").build();
    }

    @Override
    public FileResponse saveFile(MultipartFile file, HttpServletRequest request) throws Exception {
        isNotVerify(UUID.fromString(currentUser()));
        String uploadPath = fileStorageProperties.getUploadPath();
        Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
        java.io.File directory = directoryPath.toFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String fileName = UUID.randomUUID() + file.getOriginalFilename().replaceAll("\\s+","");
        validateFile(fileName);
        File dest = new File(directoryPath.toFile(), fileName);
        file.transferTo(dest);
        return new FileResponse(fileName,file.getContentType(),file.getSize());
    }

    @Override
    public UserInfoResponse addUserDetail(UserInfoRequest request) throws Exception {
        isNotVerify(UUID.fromString(currentUser()));
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
        isNotVerify(UUID.fromString(currentUser()));
        return isNotExisting(id);
    }

    @Override
    public UserInfoResponse getCurrentUserInfo() {
        isNotVerify(UUID.fromString(currentUser()));
        return isNotExisting(createdBy(UUID.fromString(currentUser())).getId());
    }

    @Override
    public UserInfoResponse updateCurrentUserInfo(UserInfoRequestUpdate request) throws Exception {
        isNotVerify(UUID.fromString(currentUser()));
        UserInfo preUserInfo = userInfoRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId());

        if(preUserInfo == null){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_SET_UP_DETAIL);
        }

        if(request.getGender() == null){
            throw new IllegalArgumentException(ValidationConfig.NULL_GENDER);
        }

        isValidString(request.getFirstname(), "FirstName");
        isValidString(request.getLastname(),"LastName");

        // Update Firstname & Lastname of user in keycloak
        UserRepresentation updatedUser = new UserRepresentation();
        resource(UUID.fromString(currentUser()));
        updatedUser.setFirstName(request.getFirstname().replaceAll("\\s+",""));
        updatedUser.setLastName(request.getLastname().replaceAll("\\s+",""));
        resource(UUID.fromString(currentUser())).update(updatedUser);

        // Update In User-info database
        preUserInfo.setGender(request.getGender());
        validateFile(request.getProfileImage());
        preUserInfo.setProfileImage(request.getProfileImage());
        preUserInfo.setDob(request.getDob());
        preUserInfo.setPhoneNumber(isAccept(request.getPhoneNumber()));

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

    @Override
    public Void switchRole(Role role) {
        isNotVerify(UUID.fromString(currentUser()));
        // Update logged as role of user in keycloak
        UserRepresentation user = resource(UUID.fromString(currentUser())).toRepresentation();
        user.singleAttribute("logged_as", role.name());
        resource(UUID.fromString(currentUser())).update(user);
        return null;
    }

    // Account not yet verify
    public void isNotVerify(UUID id){
        UserRepresentation user = keycloak.realm(realm).users().get(String.valueOf(id)).toRepresentation();
        if(!user.getAttributes().get("is_verify").get(0).equalsIgnoreCase("true")){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_USER);
        }
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
                DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
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

    // firstname and lastname Validating
    public void isValidString(String data, String field){
        try {
            if(field.equalsIgnoreCase("FirstName")){
                if(data.matches(".*\\d+.*")){
                    throw new IllegalArgumentException(field + ValidationConfig.INVALID_FIELD);
                }
            }
            if(data.matches(".*\\d+.*")){
                throw new IllegalArgumentException(field + ValidationConfig.INVALID_FIELD);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ValidationConfig.INVALID_STRING);
        }
    }


}

