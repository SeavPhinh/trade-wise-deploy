package com.example.productservice.service.comment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.Post;
import com.example.commonservice.model.Shop;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.productservice.config.FileStorageProperties;
import com.example.productservice.exception.NotFoundExceptionClass;
import com.example.productservice.model.Product;
import com.example.productservice.model.ProductForSale;
import com.example.productservice.repository.ProductForSaleRepository;
import com.example.productservice.repository.ProductRepository;
import com.example.productservice.request.ProductForSaleRequest;
import com.example.productservice.response.ProductForSaleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ProductRepository productRepository;
    private final WebClient webClient;

    public ProductForSaleServiceImpl(ProductForSaleRepository productForSaleRepository, FileStorageProperties fileStorageProperties, ProductRepository productRepository, WebClient.Builder webClient) {
        this.productForSaleRepository = productForSaleRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.productRepository = productRepository;
        this.webClient = webClient.baseUrl("http://192.168.154.1:8080/").build();
    }

    @Override
    public ProductForSaleResponse saveListFile(UUID id, List<MultipartFile> files, HttpServletRequest request) throws IOException {

        UUID shopId = shop().getId();
        ProductForSale preData = productForSaleRepository.findById(id).orElseThrow();

        validationShop(shopId,preData);
        isLegal(UUID.fromString(currentUser()));
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
    public ProductForSaleResponse addProductToPost(ProductForSaleRequest postRequest) {
        Post product = post(postRequest.getPostId());
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
        // Create new object to store before delete
        ProductForSaleResponse response = getProductById(id);
        if(response.getShopId().toString().equalsIgnoreCase(shop().getId().toString()) ||
           currentUser().equalsIgnoreCase(post(id).getUserId().toString())){
            productForSaleRepository.deleteById(id);
            return "You have delete this product successfully";
        }
        throw new IllegalArgumentException(ValidationConfig.NOT_OWNER_PRODUCT);
    }

    @Override
    public ProductForSaleResponse updateProductById(UUID id, ProductForSaleRequest request) {
        ProductForSale preData = productForSaleRepository.findById(id).orElseThrow();
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
            if(!currentUser().equalsIgnoreCase(post(id).getUserId().toString())){
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
        if(authentication.getPrincipal() instanceof Jwt jwt){
            return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                    .get()
                    .uri("api/v1/shops/current")
                    .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), Shop.class);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    // Return Post
    public Post post(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt){
            return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                    .get()
                    .uri("api/v1/posts/{id}", id)
                    .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), Post.class);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
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

}
