package com.example.shopservice.service.shop;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.shopservice.config.FileStorageProperties;
import com.example.shopservice.model.Address;
import com.example.shopservice.model.FileStorage;
import com.example.shopservice.model.Shop;
import com.example.shopservice.repository.ShopRepository;
import com.example.shopservice.request.FileRequest;
import com.example.shopservice.request.ShopRequest;
import com.example.shopservice.response.ShopResponse;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final FileStorageProperties fileStorageProperties;
    private final WebClient webClient;

    public ShopServiceImpl(ShopRepository shopRepository, FileStorageProperties fileStorageProperties, WebClient.Builder webClient) {
        this.shopRepository = shopRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
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

    @Override
    public ShopResponse setUpShop(ShopRequest request) throws Exception {
        validateFile(request.getProfileImage());
        return shopRepository.save(request.toEntity(request.getAddress().toEntity(), createdBy(UUID.fromString(currentUser())).getId())).toDto();
    }

    @Override
    public List<ShopResponse> getAllShop() {
        return shopRepository.findAll().stream().map(Shop::toDto).collect(Collectors.toList());
    }

    @Override
    public ShopResponse getShopById(UUID id) {
        return shopRepository.findById(id).orElseThrow().toDto();
    }

    @Override
    public ShopResponse deleteShopById(UUID id) {
        ShopResponse response = getShopById(id);
        shopRepository.deleteById(id);
        return response;
    }

    @Override
    public ShopResponse updateShopById(UUID id, ShopRequest request) {

        Shop preShop = shopRepository.findById(id).orElseThrow();

        Address address = preShop.getAddress();

        address.setProvince(request.getAddress().getProvince());
        address.setStreet(request.getAddress().getStreet());
        address.setUrl(request.getAddress().getUrl());

        // Update Previous Data
        preShop.setName(request.getName());
        preShop.setAddress(address);
        preShop.setProfileImage(request.getProfileImage());
        preShop.setLastModified(LocalDateTime.now());

        return shopRepository.save(preShop).toDto();
    }

    @Override
    public ShopResponse getShopByOwnerId(UUID ownerId) {
        return shopRepository.getShopByOwnerId(ownerId).toDto();
    }

    // Returning Token
    public String currentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                // Decode to Get User Id
//            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
                DecodedJWT decodedJWT = JWT.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXckJaS3JoVDBIRXhhMU9FWUNJcUhOWjlSQkpyUVNRejZMVlQ1dEFEU1BnIn0.eyJleHAiOjE2OTc2OTQ4MzgsImlhdCI6MTY5NzY5NDUzOCwianRpIjoiZTU2MGY3YmUtNDhkOS00MzI4LWI5NTctMjE3ZDM2YjNlNjlkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjcwMzUzYTEzLWI0ZDQtNDFkNi05ZDJhLWFmNDIxOGQ4NzdlNSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiZGVhNjhhOWYtNjYyNy00MGI4LTkzNjctMGIwYjcyYmIzZjE5IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImRlYTY4YTlmLTY2MjctNDBiOC05MzY3LTBiMGI3MmJiM2YxOSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImZpcnN0bmFtZSBsYXN0bmFtZSIsInByZWZlcnJlZF91c2VybmFtZSI6ImJ1eWVyIiwiZ2l2ZW5fbmFtZSI6ImZpcnN0bmFtZSIsImZhbWlseV9uYW1lIjoibGFzdG5hbWUiLCJlbWFpbCI6ImJ1eWVyQGdtYWlsLmNvbSJ9.FS6KgdPqotE9rHKvyUv9L9FWZCHjKGXHDphsSZs1spsguAwX48hHzFsp-pLZh6odlwzWPRWYSA4Kw13ehY8hLALOTrBOJdzlo8hbC0bPsd5AREEjIpiVfWclOYxsYbqGfd-schEEeCxX_VwDj0EjA22Ovxjtcv07TrO7z1cvUw36zZI0ra3VuUwC1DgsIbEkboXDMVaPOP2m1JgGyjQbMGFCddTQCF18zj_sTXEpwAEpKfdHn-725wPV-X-BMa6EKqDOEgFlfIT2-yjYSJj1JXJH_kmd4gdFwOXvSiWUG_i32dhl-x8YuL2ikzzZLFe_xD6OG9B8_bVmU-s5byCawA");
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

        return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                .get()
                .uri("api/v1/users/{id}", id)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block()).getPayload(), User.class);
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
