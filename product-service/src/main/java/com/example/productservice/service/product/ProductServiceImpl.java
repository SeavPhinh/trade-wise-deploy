package com.example.productservice.service.product;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final FileStorageProperties fileStorageProperties;
    private final WebClient webClient;
    private final WebClient shopClient;

    public ProductServiceImpl(ProductRepository productRepository, FileStorageProperties fileStorageProperties, WebClient.Builder webClient, WebClient.Builder shopClient) {
        this.productRepository = productRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.webClient = webClient.baseUrl("http://192.168.154.1:1688/").build();
        this.shopClient = shopClient.baseUrl("http://192.168.154.1:1688/").build();
    }


    @Override
    public List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException {

        isLegal(UUID.fromString(currentUser()));

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
        isLegal(UUID.fromString(currentUser()));
        return productRepository.save(postRequest.toEntity(shop().getId())).toDto(postRequest.getFiles());
    }

    // This method need to update for related products
    @Override
    public List<ProductResponse> getAllProduct() {

        List<ProductResponse> responseList = productRepository.findAll().stream().map(product -> product.toDto(getFiles(product))).collect(Collectors.toList());
        if(!responseList.isEmpty()){
            return responseList;
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_PRODUCTS);
    }

    @Override
    public ProductResponse getProductById(UUID id) {

        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            return product.get().toDto(getFiles(productRepository.findById(id).orElseThrow()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_PRODUCT);

    }

    @Override
    public ProductResponse deleteProductById(UUID id) {
        isLegal(UUID.fromString(currentUser()));
        // Create new object to store before delete
        ProductResponse response = getProductById(id);
        productRepository.deleteById(id);
        return response;
    }

    @Override
    public ProductResponse updateProductById(UUID id, ProductRequest request) {

        isLegal(UUID.fromString(currentUser()));
        Product preData = productRepository.findById(id).orElseThrow();
        // Update Previous Data
        preData.setTitle(request.getTitle());
        preData.setFile(request.getFiles().toString());
        preData.setDescription(request.getDescription());
        preData.setStatus(request.getStatus());
        preData.setLastModified(LocalDateTime.now());

        return productRepository.save(preData).toDto(getFiles(preData));
    }

    @Override
    public List<ProductResponse> getAllProductByShopId(UUID id) {
        shopById(id);
        List<ProductResponse> responseList = productRepository.getAllProductByShopId(id).stream().map(product -> product.toDto(getFiles(product))).collect(Collectors.toList());
        if(!responseList.isEmpty()){
            return responseList;
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_PRODUCTS_IN_UR_SHOP);
    }

    // Returning Token
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Decode to Get User Id
//            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
            DecodedJWT decodedJWT = JWT.decode("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXckJaS3JoVDBIRXhhMU9FWUNJcUhOWjlSQkpyUVNRejZMVlQ1dEFEU1BnIn0.eyJleHAiOjE2OTc5OTEwNjgsImlhdCI6MTY5Nzk5MDc2OCwianRpIjoiZWNlMTYyODYtNzJkMC00ZDVhLWIxYjYtNmMxZDFhMWVjNTY4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjcwMzUzYTEzLWI0ZDQtNDFkNi05ZDJhLWFmNDIxOGQ4NzdlNSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiYWRiNzA3ZWItMzVlZi00OTI3LWEwODgtNmVjNTg4YWZiOWJiIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6ImFkYjcwN2ViLTM1ZWYtNDkyNy1hMDg4LTZlYzU4OGFmYjliYiIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImZpcnN0bmFtZSBsYXN0bmFtZSIsInByZWZlcnJlZF91c2VybmFtZSI6ImJ1eWVyIiwiZ2l2ZW5fbmFtZSI6ImZpcnN0bmFtZSIsImZhbWlseV9uYW1lIjoibGFzdG5hbWUiLCJlbWFpbCI6ImJ1eWVyQGdtYWlsLmNvbSJ9.UGRtb1hXOEavVy46XwPAszeo-333QovTSI6_b1M8JNp764iBwtiWfSg1qRz--Z9HH-BoISaW1SwyHq9cdHSWnTaKy8xFNftEk8zcuhfsnH-aA6VpWDUjv1zh2h27Ud19eY2bHCK4hNTeXuCqDozcQcNvN4UA5sDJc2VqaToYW1ek8_E6Q02RlDBK8sL59rOoh5rcU9bdfwSEuaauQN95iPfFrQK6O5i5eeXilHdbVpWxZppPxkrVXEBhH2A2mt-Ne8Vo9WhxGeRQz227OqvESrGlPxk1fL_OuZ7hGaz7-T08cNzSIfn1QmdXl3hiYMbG0wLpY0cKbP9SMx_m6K2xBg");
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
    public Shop shop(){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            if(authentication.getPrincipal() instanceof Jwt jwt){
                return covertSpecificClass.convertValue(Objects.requireNonNull(shopClient
                        .get()
                        .uri("api/v1/shops/current")
//                    .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                        .headers(h -> h.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJXckJaS3JoVDBIRXhhMU9FWUNJcUhOWjlSQkpyUVNRejZMVlQ1dEFEU1BnIn0.eyJleHAiOjE2OTc5OTE4NzIsImlhdCI6MTY5Nzk5MTU3MiwianRpIjoiZDU1Mjg4YzItYzBlOS00YWJiLWE0MzYtNmE1MDBmNGQ0YzAzIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDoxMjM0L2F1dGgvcmVhbG1zL2dvLXNlbGxpbmctYXBpIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjcwMzUzYTEzLWI0ZDQtNDFkNi05ZDJhLWFmNDIxOGQ4NzdlNSIsInR5cCI6IkJlYXJlciIsImF6cCI6ImdvLXNlbGxpbmciLCJzZXNzaW9uX3N0YXRlIjoiODE3ZmM0NTMtY2Y1Ny00MmIzLWJlMDItMzkzMmIxNGJhOTBhIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyIqIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtZ28tc2VsbGluZy1hcGkiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjgxN2ZjNDUzLWNmNTctNDJiMy1iZTAyLTM5MzJiMTRiYTkwYSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImZpcnN0bmFtZSBsYXN0bmFtZSIsInByZWZlcnJlZF91c2VybmFtZSI6ImJ1eWVyIiwiZ2l2ZW5fbmFtZSI6ImZpcnN0bmFtZSIsImZhbWlseV9uYW1lIjoibGFzdG5hbWUiLCJlbWFpbCI6ImJ1eWVyQGdtYWlsLmNvbSJ9.k29JFWVPyR1WFmONhnYzeG5xt6sfJBOCtyIYpBrcyu344M24Scby4gv87Ua71dOJ7VnvFNCLB7kPR7x_QiCjKW6SD-SAMZ3kjewv2UjDKEGv9pzuhluM0q5HngIWI3_WjyCYpwyQ0_NHf0vp5iGgXoq-IhitWPM22SfvsH0yNq1XVYziRj7jYnyAp8Mwg5co4No7Q6gwGgQaIfZHnd4teUs3rPhFBgrIWHZVp51mGKqvzErtG1gdLSO5V7omHqtJwC7xK9c98ZUcwfHBJ7o-FBNiptURy_LZFi2K2HjBFrwdbjEB8vu6giQ3lcyN6p0IgyJ6sPmhzcyxXm48t7Up9A"))
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), Shop.class);
            }
        }catch (Exception e){
            throw new IllegalArgumentException(ValidationConfig.SHOP_NOT_CREATED);
        }
        throw new IllegalArgumentException(ValidationConfig.SHOP_NOT_CREATED);
    }

    // Return Shop
    public Shop shopById(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            if(authentication.getPrincipal() instanceof Jwt jwt){
                return covertSpecificClass.convertValue(Objects.requireNonNull(shopClient
                        .get()
                        .uri("api/v1/shops/{id}", id).retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), Shop.class);
            }
        }catch (Exception e){
            throw new IllegalArgumentException(ValidationConfig.SHOP_NOTFOUND);
        }
        throw new IllegalArgumentException(ValidationConfig.SHOP_NOTFOUND);
    }

    // Separate file -> List
    private List<String> getFiles(Product product) {
        return Arrays.asList(product.getFile().replaceAll("\\[|\\]", "").split(", "));
    }

    // Validation legal Role
    public void isLegal(UUID id){
        if(!createdBy(id).getLoggedAs().equalsIgnoreCase(String.valueOf(Role.SELLER))){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_PROCESS);
        }
    }



}
