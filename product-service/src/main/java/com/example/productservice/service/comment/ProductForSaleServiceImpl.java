package com.example.productservice.service.comment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.Post;
import com.example.commonservice.model.Shop;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.PostResponse;
import com.example.commonservice.response.ShopResponse;
import com.example.productservice.config.FileStorageProperties;
import com.example.productservice.exception.NotFoundExceptionClass;
import com.example.productservice.model.ProductForSale;
import com.example.productservice.repository.ProductForSaleRepository;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.request.ProductForSaleRequest;
import com.example.productservice.request.ProductForSaleRequestUpdate;
import com.example.productservice.response.ProductForSaleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import org.keycloak.admin.client.Keycloak;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductForSaleServiceImpl implements ProductForSaleService {

    private final ProductForSaleRepository productForSaleRepository;
    private final FileStorageProperties fileStorageProperties;
    private final WebClient.Builder webClient;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public ProductForSaleServiceImpl(ProductForSaleRepository productForSaleRepository, FileStorageProperties fileStorageProperties, WebClient.Builder webClient, Keycloak keycloak) {
        this.productForSaleRepository = productForSaleRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.webClient = webClient;
        this.keycloak = keycloak;
    }

    @Override
    public ProductForSaleResponse saveListFile(UUID id, List<MultipartFile> files, HttpServletRequest request) throws IOException {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        UUID shopId = shop().getId();
        ProductForSale preData = productForSaleRepository.findById(id).orElseThrow();
        validationShop(shopId,preData);
        List<String> listFiles = new ArrayList<>();
        for (MultipartFile file : files) {

            String uploadPath = fileStorageProperties.getUploadPath();
            Path directoryPath = Paths.get(uploadPath).toAbsolutePath().normalize();
            java.io.File directory = directoryPath.toFile();

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = UUID.randomUUID() + file.getOriginalFilename().replaceAll("\\s+","");
            File dest = new File(directoryPath.toFile(), fileName);
            file.transferTo(dest);
            listFiles.add(fileName);
        }
        return productForSaleRepository.save(preData).toDto(listFiles);
    }

    @Override
    public ProductForSaleResponse addProductToPost(ProductForSaleRequest postRequest) throws Exception {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        PostResponse product = post(postRequest.getPostId());

        for (String image : postRequest.getFiles()) {
            validateFile(image);
        }
        if(product != null){
            return productForSaleRepository.save(postRequest.toEntity(shop().getId())).toDto(postRequest.getFiles());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_POST);
    }

    @Override
    public List<ProductForSaleResponse> getAllProduct() {
        return productForSaleRepository.findAll().stream().map(product -> product.toDto(getFiles(product))).collect(Collectors.toList());
    }

    @Override
    public ProductForSaleResponse getProductById(UUID id) {
        Optional<ProductForSale> product = productForSaleRepository.findById(id);
        if(product.isPresent()){
            if(product.get().getShopId().toString().equalsIgnoreCase(shop().getId().toString()) ||
               product.get().getPostId().toString().equalsIgnoreCase(post(product.get().getPostId()).getId().toString())){
                return product.get().toDto(getFiles(productForSaleRepository.findById(id).orElseThrow()));
            }
            throw new NotFoundExceptionClass(ValidationConfig.NOT_YET_ADD_TO_POST);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_PRODUCT);
    }

    @Override
    public String deleteProductById(UUID id) {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        // Create new object to store before delete
        ProductForSaleResponse response = getProductById(id);
        if(response.getShopId().toString().equalsIgnoreCase(shop().getId().toString()) ||
           currentUser().equalsIgnoreCase(post(id).getCreatedBy().getId().toString())){
            productForSaleRepository.deleteById(id);
            return "You have delete this product successfully";
        }
        throw new IllegalArgumentException(ValidationConfig.NOT_OWNER_PRODUCT);
    }

    @Override
    public ProductForSaleResponse updateProductById(UUID id, ProductForSaleRequestUpdate request) throws Exception {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        ProductForSale preData = productForSaleRepository.findById(id).orElseThrow();

        for (String image : request.getFiles()) {
            validateFile(image);
        }

        if(preData.getShopId().toString().equalsIgnoreCase(shop().getId().toString()) ||
        preData.getPostId().toString().equalsIgnoreCase(post(id).getId().toString())){
            // Update Previous Data
            preData.setTitle(request.getTitle());
            preData.setFile(request.getFiles().toString());
            preData.setDescription(request.getDescription());
            preData.setStatus(request.getStatus());
            preData.setLastModified(LocalDateTime.now());
            return productForSaleRepository.save(preData).toDto(getFiles(preData));
        }
        throw new IllegalArgumentException(ValidationConfig.NOT_OWNER_PRODUCT);

    }

    @Override
    public List<ProductForSaleResponse> getProductByPostId(UUID id) {
        List<ProductForSale> listFiles = productForSaleRepository.getProductByPostId(id);
        if(!listFiles.isEmpty()){
            // Not Owner Post (seller)
            if(!currentUser().equalsIgnoreCase(post(id).getCreatedBy().getId().toString())){
                if(productForSaleRepository.getProductByPostIdAndUserId(id, shop().getId()).isEmpty()){
                    throw new NotFoundExceptionClass(ValidationConfig.UR_PRODUCT_NOT_FOUND);
                }
                return productForSaleRepository.getProductByPostIdAndUserId(id, shop().getId()).stream().map(product -> product.toDto(getFiles(product))).collect(Collectors.toList());
            }
            // Owner Post (buyer) can see all product comment
            return productForSaleRepository.getProductByPostId(id).stream().map(product -> product.toDto(getFiles(product))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_EXIST_IN_POST);
    }

    @Override
    public ByteArrayResource getImage(String fileName) throws IOException {
        String filePath = "product-service/src/main/resources/storage/" + fileName;
        Path path = Paths.get(filePath);

        if(!Files.exists(path)){
            throw new com.example.productservice.exception.NotFoundExceptionClass(ValidationConfig.FILE_NOTFOUND);
        }
        String uploadPath = fileStorageProperties.getUploadPath();
        Path paths = Paths.get(uploadPath + fileName);
        return new ByteArrayResource(Files.readAllBytes(paths));
    }

    // Returning Token
    public String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            // Decode to Get User Id
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
                .build()
                .get()
                .uri("api/v1/users/{id}", id)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block()).getPayload(), User.class);
    }

    // Return Shop
    public ShopResponse shop(){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            if(authentication.getPrincipal() instanceof Jwt jwt){
                return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                        .build()
                        .get()
                        .uri("api/v1/shops/current")
                        .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), ShopResponse.class);
            }
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    // Return Post
    public PostResponse post(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            if(authentication.getPrincipal() instanceof Jwt jwt){
                return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                        .build()
                        .get()
                        .uri("api/v1/posts/{id}", id)
                        .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), PostResponse.class);
            }
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_POST);
    }

    // Separate file -> List
    private List<String> getFiles(ProductForSale product) {
        return Arrays.asList(product.getFile().replaceAll(ValidationConfig.REGEX_ROLES, "").split(", "));
    }

    // Validation legal Role
    public void isLegal(UUID id){
        if(!createdBy(id).getLoggedAs().equalsIgnoreCase(String.valueOf(Role.SELLER))){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_PROCESS);
        }
    }

    // Validation Shop
    public void validationShop (UUID shopId, ProductForSale preShop){
        if(!shopId.toString().equalsIgnoreCase(preShop.getShopId().toString())){
            throw new IllegalArgumentException(ValidationConfig.CANNOT_UPLOAD);
        }
    }

    // Account not yet verify
    public void isNotVerify(UUID id){
        UserRepresentation user = keycloak.realm(realm).users().get(String.valueOf(id)).toRepresentation();
        if(!user.getAttributes().get("is_verify").get(0).equalsIgnoreCase("true")){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_USER);
        }
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
