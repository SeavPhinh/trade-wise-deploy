package com.example.shopservice.service.shop;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.CategorySubCategoryResponse;
import com.example.shopservice.config.FileStorageProperties;
import com.example.shopservice.enumeration.Filter;
import com.example.shopservice.enumeration.Level;
import com.example.shopservice.exception.NotFoundExceptionClass;
import com.example.shopservice.model.Address;
import com.example.shopservice.model.Shop;
import com.example.shopservice.repository.RatingRepository;
import com.example.shopservice.repository.ShopRepository;
import com.example.shopservice.request.ShopRequest;
import com.example.shopservice.response.ShopResponse;
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
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;
    private final FileStorageProperties fileStorageProperties;
    private final RatingRepository ratingRepository;
    private final WebClient.Builder webClient;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public ShopServiceImpl(ShopRepository shopRepository, FileStorageProperties fileStorageProperties, RatingRepository ratingRepository, WebClient.Builder webClient, Keycloak keycloak) {
        this.shopRepository = shopRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.ratingRepository = ratingRepository;
        this.webClient = webClient;
        this.keycloak = keycloak;
    }


    @Override
    public ShopResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException {
        isNotVerify(UUID.fromString(currentUser()));
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
            return preUserInfo.toDto(categoriesList(preUserInfo.getSubCategoryList()),ratedCount(preUserInfo.getId()), ratedPercentage(preUserInfo.getId()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CREATED);

    }

    @Override
    public ShopResponse setUpShop(ShopRequest request) throws Exception {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        isExistingShop(createdBy(UUID.fromString(currentUser())).getId());
        isContainWhitespace(request.getAddress().getUrl());
        validateFile(request.getProfileImage());
        List<String> nameSubCategory = categoriesList(request.getSubCategoryList().toString());
        return shopRepository.save(request.toEntity(request.getAddress().toEntity(), createdBy(UUID.fromString(currentUser())).getId())).toDto(nameSubCategory,0, 0F);
    }

    @Override
    public List<ShopResponse> getAllShop(){
        List<ShopResponse> shops = shopRepository.getAllActiveShop()
                .stream()
                .map(sub -> sub.toDto(categoriesList(sub.getSubCategoryList()),ratedCount(sub.getId()), ratedPercentage(sub.getId())))
                .collect(Collectors.toList());
        if(shops.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CONTAIN);
        }
        return shops;
    }

    @Override
    public ShopResponse getShopById(UUID id){
        Shop shop = shopRepository.getActiveShopById(id);
        if(shop != null){
            return shop.toDto(categoriesList(shop.getSubCategoryList()),ratedCount(shop.getId()), ratedPercentage(shop.getId()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
    }

    @Override
    public ShopResponse updateShopById(ShopRequest request) throws Exception {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        Shop preShop = shopRepository.getShopByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(preShop != null){
            if(!preShop.getUserId().toString().equalsIgnoreCase(createdBy(UUID.fromString(currentUser())).getId().toString())){
                throw new IllegalArgumentException(ValidationConfig.ILLEGAL_SHOP_UPDATE);
            }
            validateFile(request.getProfileImage());
            Address address = preShop.getAddress();
            address.setAddress(request.getAddress().getAddress());
            address.setUrl(request.getAddress().getUrl());
            // Update Previous Data
            preShop.setName(request.getName());
            preShop.setAddress(address);
            preShop.setSubCategoryList(request.getSubCategoryList().toString());
            preShop.setProfileImage(request.getProfileImage());
            preShop.setLastModified(LocalDateTime.now());
            return shopRepository.save(preShop).toDto(categoriesList(request.getSubCategoryList().toString()),ratedCount(preShop.getId()), ratedPercentage(preShop.getId()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CREATED);
    }

    @Override
    public ShopResponse getShopByOwnerId() {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        Shop shop = shopRepository.getShopByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(shop != null){
            return shop.toDto(categoriesList(shop.getSubCategoryList()),ratedCount(shop.getId()), ratedPercentage(shop.getId()));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOT_CREATED);
    }

    @Override
    public ShopResponse shopAction(Boolean isActive) {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        Shop preShop = shopRepository.getShopByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
        if(preShop != null){
            if(!preShop.getUserId().toString().equalsIgnoreCase(createdBy(UUID.fromString(currentUser())).getId().toString())){
                throw new IllegalArgumentException(ValidationConfig.ILLEGAL_SHOP_UPDATE);
            }
            // Update Previous Data;
            preShop.setStatus(isActive);
            return shopRepository.save(preShop).toDto(categoriesList(preShop.getSubCategoryList()), ratedCount(preShop.getId()), ratedPercentage(preShop.getId()));
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
    public List<ShopResponse> getShopBasedOnRating(){

        List<Shop> getAllActiveShop = shopRepository.getAllActiveShop();
        if(getAllActiveShop.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_BEST_RATE_SHOP);
        }
        List<UUID> getAllShopIdFromRating = ratingRepository.getAllShopIdFromRating();

        if(getAllShopIdFromRating.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_BEST_RATE_SHOP);
        }
        List<UUID> activeShopInRating = new ArrayList<>(getAllShopIdFromRating);

        List<ShopResponse> topThreeShop = new ArrayList<>();
        activeShopInRating.retainAll(getAllActiveShop.stream().map(Shop::getId).collect(Collectors.toList()));

        // Active Shop from Shop table and check with Rating
        Set<UUID> uniqueIds = new HashSet<>(activeShopInRating);

        Map<UUID, Float> shopIdAndRatedValue = new HashMap<>();

        List<UUID> sortedKeys = new ArrayList<>();
        for (UUID id : uniqueIds) {
            int sum = 0;
            List<String> level = ratingRepository.getRatedStarByShopId(id);
            for (String star : level) {
                if(star.equalsIgnoreCase(Level.ONE_STAR.name())){
                    sum += 1;
                }else if(star.equalsIgnoreCase(Level.TWO_STARS.name())){
                    sum += 2;
                }else if(star.equalsIgnoreCase(Level.THREE_STARS.name())){
                    sum += 3;
                }else if(star.equalsIgnoreCase(Level.FOUR_STARS.name())){
                    sum += 4;
                }else if(star.equalsIgnoreCase(Level.FIVE_STARS.name())){
                    sum += 5;
                }
            }

            shopIdAndRatedValue.put(id, Float.valueOf(sum/level.size()));

            sortedKeys = sortKeysByValueDescending(shopIdAndRatedValue);
        }

        for (int i = 0; i < sortedKeys.size(); i++) {
            if(sortedKeys.size() > 3){
                break;
            }else{
                topThreeShop.add(shopRepository.getActiveShopById(sortedKeys.get(i)).toDto(categoriesList(shopRepository.getActiveShopById(sortedKeys.get(i)).getSubCategoryList()), ratedCount(sortedKeys.get(i)), ratedPercentage(sortedKeys.get(i))));
            }
        }

        return topThreeShop;
    }

    @Override
    public List<ShopResponse> getShopBasedOnSort(Filter filter) {
        List<Shop> shopList;
        if(filter.name().equalsIgnoreCase(Filter.OLDEST.name())){
            shopList = shopRepository.getOldestShop();
            if(!shopList.isEmpty()){
                return shopList.stream().map(h-> h.toDto(categoriesList(h.getSubCategoryList()),ratedCount(h.getId()), ratedPercentage(h.getId()))).collect(Collectors.toList());
            }
        } else if (filter.name().equalsIgnoreCase(Filter.NEWEST.name())) {
            shopList = shopRepository.getNewestShop();
            if(!shopList.isEmpty()){
                return shopList.stream().map(h-> h.toDto(categoriesList(h.getSubCategoryList()),ratedCount(h.getId()), ratedPercentage(h.getId()))).collect(Collectors.toList());
            }
        }else if (filter.name().equalsIgnoreCase(Filter.AZ.name())) {
            shopList = shopRepository.getAZShop();
            if(!shopList.isEmpty()){
                return shopList.stream().map(h-> h.toDto(categoriesList(h.getSubCategoryList()),ratedCount(h.getId()), ratedPercentage(h.getId()))).collect(Collectors.toList());
            }
        }else if (filter.name().equalsIgnoreCase(Filter.ZA.name())) {
            shopList = shopRepository.getZAShop();
            if(!shopList.isEmpty()){
                return shopList.stream().map(h-> h.toDto(categoriesList(h.getSubCategoryList()),ratedCount(h.getId()), ratedPercentage(h.getId()))).collect(Collectors.toList());
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
    }

    @Override
    public List<ShopResponse> getShopBasedOnFilter(String subCategory) {
        isContainingCategory(subCategory);
        List<Shop> shopList = shopRepository.getAllActiveShop();
        List<Shop> response = new ArrayList<>();
        if(shopList.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
        }
        for (Shop shop : shopList) {
            List<String> listCategoryShop = categoriesList(shop.getSubCategoryList());
            for (String category: listCategoryShop) {
                if(category.equalsIgnoreCase(subCategory)){
                    response.add(shop);
                }
            }
        }
        if(!response.isEmpty()){
            return response.stream().map(h-> h.toDto(categoriesList(h.getSubCategoryList()),ratedCount(h.getId()), ratedPercentage(h.getId()))).collect(Collectors.toList());
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
    }

    private static List<UUID> sortKeysByValueDescending(Map<UUID, Float> map) {
        List<Map.Entry<UUID, Float>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        List<UUID> sortedKeys = new ArrayList<>();
        for (Map.Entry<UUID, Float> entry : entryList) {
            sortedKeys.add(entry.getKey());
        }
        return sortedKeys;
    }

    // Returning Token
    public String currentUser() {
        try{
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
        return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                .baseUrl("http://8.222.225.41:8081/")
                .build()
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
    public List<String> category(String categories){
        List<String> categoriesList = Arrays.asList(categories.replaceAll("\\[|\\]", "").split(", "));
        return categoriesList.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
    }

    // Is Containing Category
    public void isContainingCategory(String category){
        try {
            ObjectMapper covertSpecificClass = new ObjectMapper();
            covertSpecificClass.registerModule(new JavaTimeModule());
            covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                    .baseUrl("http://8.222.225.41:8087/")
                    .build()
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("api/v1/sub-categories")
                            .queryParam("name", category.toUpperCase())
                            .build())
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .block()).getPayload(), CategorySubCategoryResponse.class);
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOT_FOUND_SUB_CATEGORIES);
        }
    }

    // Returning list category
    public List<String> categoriesList(String categories) {
        List<String> uuidList = category(categories);
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        List<String> responses = new ArrayList<>();
        try {
            for (String name : uuidList) {
                CategorySubCategoryResponse subName = covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                        .baseUrl("http://8.222.225.41:8087/")
                        .build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path("api/v1/sub-categories")
                                .queryParam("name", name.toUpperCase())
                                .build())
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

    // Rated Count by Shop Id
    public Integer ratedCount(UUID shopId){
        List<String> level = ratingRepository.getRatedStarByShopId(shopId);
        if(level != null){
            return level.size();
        }
        return 0;
    }

    // Percentage Rating shop
    public Float ratedPercentage(UUID shopId){
        int sum = 0;
        List<String> level = ratingRepository.getRatedStarByShopId(shopId);
        for (String star : level) {
            if(star.equalsIgnoreCase(Level.ONE_STAR.name())){
                sum += 1;
            }else if(star.equalsIgnoreCase(Level.TWO_STARS.name())){
                sum += 2;
            }else if(star.equalsIgnoreCase(Level.THREE_STARS.name())){
                sum += 3;
            }else if(star.equalsIgnoreCase(Level.FOUR_STARS.name())){
                sum += 4;
            }else if(star.equalsIgnoreCase(Level.FIVE_STARS.name())){
                sum += 5;
            }
        }
        if(sum == 0){
            return 0F;
        }
        return (float) (sum/level.size());
    }

    // Account not yet verify
    public void isNotVerify(UUID id){
        UserRepresentation user = keycloak.realm(realm).users().get(String.valueOf(id)).toRepresentation();
        if(!user.getAttributes().get("is_verify").get(0).equalsIgnoreCase("true")){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_USER);
        }
    }

}
