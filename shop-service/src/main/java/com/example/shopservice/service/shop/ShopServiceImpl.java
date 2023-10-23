package com.example.shopservice.service.shop;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.CategorySubCategoryResponse;
import com.example.shopservice.config.FileStorageProperties;
import com.example.shopservice.enumeration.Level;
import com.example.shopservice.exception.NotFoundExceptionClass;
import com.example.shopservice.model.Address;
import com.example.shopservice.model.Rating;
import com.example.shopservice.model.Shop;
import com.example.shopservice.repository.RatingRepository;
import com.example.shopservice.repository.ShopRepository;
import com.example.shopservice.request.ShopRequest;
import com.example.shopservice.response.ShopResponse;
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
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final FileStorageProperties fileStorageProperties;
    private final RatingRepository ratingRepository;
    private final WebClient webClient;
    private final WebClient subCategoryWeb;

    public ShopServiceImpl(ShopRepository shopRepository, FileStorageProperties fileStorageProperties, RatingRepository ratingRepository, WebClient.Builder webClient, WebClient.Builder subCategoryWeb) {
        this.shopRepository = shopRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.ratingRepository = ratingRepository;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
        this.subCategoryWeb = subCategoryWeb.baseUrl("http://192.168.154.1:1688/").build();
    }


    @Override
    public ShopResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException {

        isLegal(UUID.fromString(currentUser()));

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
        Shop preUserInfo = shopRepository.getShopByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(preUserInfo != null){
            preUserInfo.setProfileImage(fileName);
            shopRepository.save(preUserInfo);
            return preUserInfo.toDto(categoriesList(preUserInfo.getSubCategoryList()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CREATED);

    }

    @Override
    public ShopResponse setUpShop(ShopRequest request) throws Exception {
        isLegal(UUID.fromString(currentUser()));
        isExistingShop(createdBy(UUID.fromString(currentUser())).getId());
        isContainWhitespace(request.getAddress().getUrl());
        validateFile(request.getProfileImage());
        List<String> nameSubCategory = categoriesList(request.getSubCategoryList().toString());
        return shopRepository.save(request.toEntity(request.getAddress().toEntity(), createdBy(UUID.fromString(currentUser())).getId())).toDto(nameSubCategory);
    }

    @Override
    public List<ShopResponse> getAllShop(){
        List<ShopResponse> shops = shopRepository.getAllActiveShop().stream().map(sub -> sub.toDto(categoriesList(sub.getSubCategoryList()))).collect(Collectors.toList());
        if(!shops.isEmpty()){
            return shops;
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CONTAIN);
    }

    @Override
    public ShopResponse getShopById(UUID id){
        Shop shop = shopRepository.getActiveShopById(id);
        if(shop != null){
            return shop.toDto(categoriesList(shop.getSubCategoryList()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
    }

    @Override
    public ShopResponse updateShopById(ShopRequest request) {
        isLegal(UUID.fromString(currentUser()));
        Shop preShop = shopRepository.getShopByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(preShop != null){
            if(!preShop.getUserId().toString().equalsIgnoreCase(createdBy(UUID.fromString(currentUser())).getId().toString())){
                throw new IllegalArgumentException(ValidationConfig.ILLEGAL_SHOP_UPDATE);
            }
            Address address = preShop.getAddress();
            address.setAddress(request.getAddress().getAddress());
            address.setUrl(request.getAddress().getUrl());
            // Update Previous Data
            preShop.setName(request.getName());
            preShop.setAddress(address);
            preShop.setProfileImage(request.getProfileImage());
            preShop.setLastModified(LocalDateTime.now());
            return shopRepository.save(preShop).toDto(categoriesList(request.getSubCategoryList().toString()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CREATED);
    }

    @Override
    public ShopResponse getShopByOwnerId() {
        isLegal(UUID.fromString(currentUser()));
        Shop shop = shopRepository.getShopByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(shop != null){
            return shop.toDto(categoriesList(shop.getSubCategoryList()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CREATED);
    }

    @Override
    public ShopResponse shopAction(Boolean isActive) {
        isLegal(UUID.fromString(currentUser()));
        Shop preShop = shopRepository.getShopByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(preShop != null){
            if(!preShop.getUserId().toString().equalsIgnoreCase(createdBy(UUID.fromString(currentUser())).getId().toString())){
                throw new IllegalArgumentException(ValidationConfig.ILLEGAL_SHOP_UPDATE);
            }
            // Update Previous Data;
            preShop.setStatus(isActive);
            return shopRepository.save(preShop).toDto(categoriesList(preShop.getSubCategoryList()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CREATED);
    }

    @Override
    public ByteArrayResource getImage(String fileName) throws IOException {
        String filePath = "shop-service/src/main/resources/storage/" + fileName;
        Path path = Paths.get(filePath);

        if(!Files.exists(path)){
            throw new NotFoundExceptionClass(ValidationConfig.FILE_NOTFOUND);
        }
        String uploadPath = fileStorageProperties.getUploadPath();
        Path paths = Paths.get(uploadPath + fileName);
        return new ByteArrayResource(Files.readAllBytes(paths));
    }

    @Override
    public List<ShopResponse> getShopBasedOnRating() {

        Map<UUID,Float> percentageOfShop = new HashMap<>();

        for (Level star : Level.values()) {
            System.out.println(star.name() + ": " + rated(star));
        }

        return null;
    }

    // Return Rated All shop
    public Map<UUID,Float> rated(Level star){

        Float oneStar = 0.2F;
        Float twoStars = 0.4F;
        Float threeStars = 0.6F;
        Float fourStars = 0.8F;
        Float fiveStars = 1F;

        Map<UUID,Float> starOfShop = new HashMap<>();
        Map<String, List<Rating>> rated = new HashMap<>();

        //
        for (Level level : Level.values()) {
            rated.put(level.name(),ratingRepository.getAllShopRatedByStars(level.name()));
        }

        Integer countStarOfProjectId;

        for (Rating name : rated.get(star.name())) {
            Float sum = 0F;
            switch (star.name()) {
                case "ONE_STAR": {
                    countStarOfProjectId = ratingRepository.countStarByProjectId(name.getShop().getId());
                    sum += oneStar * countStarOfProjectId;
                    starOfShop.put(name.getShop().getId(), sum);
                    break;
                }
                case "TWO_STARS": {
                    countStarOfProjectId = ratingRepository.countStarByProjectId(name.getShop().getId());
                    sum += twoStars * countStarOfProjectId;
                    starOfShop.put(name.getShop().getId(), sum);
                    break;
                }
                case "THREE_STARS": {
                    countStarOfProjectId = ratingRepository.countStarByProjectId(name.getShop().getId());
                    sum += threeStars * countStarOfProjectId;
                    starOfShop.put(name.getShop().getId(), sum);
                    break;
                }
                case "FOUR_STARS": {
                    countStarOfProjectId = ratingRepository.countStarByProjectId(name.getShop().getId());
                    sum += fourStars * countStarOfProjectId;
                    starOfShop.put(name.getShop().getId(), sum);
                    break;
                }
                case "FIVE_STARS": {
                    countStarOfProjectId = ratingRepository.countStarByProjectId(name.getShop().getId());
                    sum += fiveStars * countStarOfProjectId;
                    starOfShop.put(name.getShop().getId(), sum);
                    break;
                }
            }
        }

        return starOfShop;
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

    // Converting Category from Attribute as String to ArrayList
    public List<UUID> category(String categories){
        List<String> categoriesList = Arrays.asList(categories.replaceAll("\\[|\\]", "").split(", "));
        return categoriesList.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }

    // Converting Category list as UUID to List<String>
    public List<String> categoriesList(String categories) {

        List<UUID> uuidList = category(categories);
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        List<String> responses = new ArrayList<>();
        try {
            for (UUID subId : uuidList) {
                CategorySubCategoryResponse subName = covertSpecificClass.convertValue(Objects.requireNonNull(subCategoryWeb
                        .get()
                        .uri("api/v1/subcategories/{id}", subId)
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), CategorySubCategoryResponse.class);
                responses.add(subName.getSubCategory().getName());
            }
            return responses;

        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }

    }

    // Validation Whitespace
    public void isContainWhitespace(String text){
        if(text.contains(" ")){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_WHITESPACE);
        }
    }

    // Validation Existing Shop
    public void isExistingShop(UUID id){
        Shop shop = shopRepository.getShopByOwnerId(id);
        if(shop != null){
            throw new IllegalArgumentException(ValidationConfig.USER_CONTAIN_SHOP);
        }
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

    // Validation legal Role
    public void isLegal(UUID id){
        if(!createdBy(id).getLoggedAs().equalsIgnoreCase(String.valueOf(Role.SELLER))){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_PROCESS);
        }
    }

}
