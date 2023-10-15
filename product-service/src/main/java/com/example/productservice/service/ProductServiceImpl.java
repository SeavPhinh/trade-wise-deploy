package com.example.productservice.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.model.Shop;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.productservice.config.FileStorageProperties;
import com.example.productservice.exception.NotFoundExceptionClass;
import com.example.productservice.model.FileStorage;
import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.request.FileRequest;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ProductResponse;
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
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final FileStorageProperties fileStorageProperties;
    private final WebClient webClient;
    private final WebClient shopClient;

    public ProductServiceImpl(ProductRepository productRepository, FileStorageProperties fileStorageProperties, WebClient.Builder webClient, WebClient.Builder shopClient) {
        this.productRepository = productRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
        this.shopClient = shopClient.baseUrl("http://localhost:8088/").build();
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
    public ProductResponse addProduct(ProductRequest postRequest) {
        return productRepository.save(postRequest.toEntity(shop(createdBy(UUID.fromString(currentUser())).getId()).getId())).toDto(postRequest.getFiles());
    }

    // Returning Token
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Decode to Get User Id
//            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
            DecodedJWT decodedJWT = JWT.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXckJaS3JoVDBIRXhhMU9FWUNJcUhOWjlSQkpyUVNRejZMVlQ1dEFEU1BnIn0.eyJleHAiOjE2OTczNTg4OTYsImlhdCI6MTY5NzM1ODU5NiwianRpIjoiN2ZiYzE4ZmUtMWRlYy00NGU0LWFkN2QtODZkZDI2MWNmYTNhIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjJhMTAxYjk5LTYxYmQtNDBhMS05MmMzLThlZDgwMzJhMmE0OCIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiYmRjMzdjM2YtY2E3Zi00YjE5LWEyNGQtNzE2NGNkOWEyY2I4IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImJkYzM3YzNmLWNhN2YtNGIxOS1hMjRkLTcxNjRjZDlhMmNiOCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6Im15IHJlY2VpdmVyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoicmVjZWl2ZXIiLCJnaXZlbl9uYW1lIjoibXkiLCJmYW1pbHlfbmFtZSI6InJlY2VpdmVyIiwiZW1haWwiOiJyZWNlaXZlckBnbWFpbC5jb20ifQ.ETFk0WjaQL5NwYoES3_-wnCxtI-VPrCDY3UxSjW4zJYEa-3QDpGY8j46j4bR1kXXHd75snW8n44wiCbWlDbLMOB5hVkAK-4eZgRNrzCIbLnN1p15KeKzIKaSK8EuoG2gPKd3hsuRPhZ9QGst1K1-xTjVtdhrumEqL1MUMzI8KM45wC9iJfuYHJLECqCk72TnbNhPludJzLA38naO6nlUFZ_n1uDKZcxq8SpKYUES0BQeWuHLI0agsRhHxqhAi6YVtjsBhLhG2r6tNxdQcTe8TELJJGeF6YqYwTcpr6RRaO7UQFmmRbRA9WrwoZIddXMxfeGpXwhhcaKY0T4EBqg4mg");
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

    // Return Shop
    public Shop shop(UUID id){

        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication.getPrincipal() instanceof Jwt jwt){
            return covertSpecificClass.convertValue(Objects.requireNonNull(shopClient
                    .get()
                    .uri("api/v1/shops/owner/{id}", id)
                    .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), Shop.class);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    // Separate file -> List
    private List<String> getFiles(Product product) {
        return Arrays.asList(product.getFile().replaceAll("\\[|\\]", "").split(", "));
    }


}
