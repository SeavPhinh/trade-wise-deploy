package com.example.productservice.service.product;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.Shop;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.ShopResponse;
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
        return productRepository.save(postRequest.toEntity(shop(UUID.fromString(currentUser())).getId())).toDto(postRequest.getFiles());
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
            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
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
    public ShopResponse shop(UUID userId){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            if(authentication.getPrincipal() instanceof Jwt jwt){

                System.out.println("Token: " + jwt.getTokenValue());

                return covertSpecificClass.convertValue(Objects.requireNonNull(shopClient
                        .get()
                        .uri("api/v1/shops/user/{userId}", userId)
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), ShopResponse.class);
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
