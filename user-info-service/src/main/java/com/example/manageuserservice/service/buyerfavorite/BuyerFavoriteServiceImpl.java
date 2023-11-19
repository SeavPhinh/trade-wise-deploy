package com.example.manageuserservice.service.buyerfavorite;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.ShopResponse;
import com.example.manageuserservice.exception.NotFoundExceptionClass;
import com.example.manageuserservice.model.BuyerFavorite;
import com.example.manageuserservice.repository.BuyerFavoriteRepository;
import com.example.manageuserservice.request.BuyerFavoriteRequest;
import com.example.manageuserservice.response.BuyerFavoriteResponse;
import com.example.manageuserservice.response.UserInfoResponse;
import com.example.manageuserservice.service.userinfo.UserInfoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuyerFavoriteServiceImpl implements BuyerFavoriteService {

    private final BuyerFavoriteRepository buyerFavoriteRepository;
    private final UserInfoServiceImpl userInfoService;
    private final WebClient.Builder webClient;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;


    public BuyerFavoriteServiceImpl(BuyerFavoriteRepository buyerFavoriteRepository, UserInfoServiceImpl userInfoService, WebClient.Builder webClient, Keycloak keycloak) {
        this.buyerFavoriteRepository = buyerFavoriteRepository;
        this.userInfoService = userInfoService;
        this.webClient = webClient;
        this.keycloak = keycloak;
    }

    @Override
    public BuyerFavoriteResponse addedShopToFavoriteList(BuyerFavoriteRequest request) {
        String profileImage = null;
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        BuyerFavorite buyerFav = buyerFavoriteRepository.findByUserIdAndShopId(request.getShopId(),createdBy(UUID.fromString(currentUser())).getId());
        if(buyerFav != null){
            throw new IllegalArgumentException(ValidationConfig.ALREADY_FAV_TO_SHOP);
        }
        ShopResponse shop = shop(request.getShopId());
        UserInfoResponse userInfoResponse = userInfoService.getUserInfoByUserIdFavorite(shop.getUserId());
        if(userInfoResponse != null){
            profileImage = userInfoResponse.getProfileImage();
        }
        return buyerFavoriteRepository.save(request.toEntity(createdBy(UUID.fromString(currentUser())).getId())).toDto(shop, profileImage);
    }

    @Override
    public List<BuyerFavoriteResponse> getCurrentUserInfo() {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        List<BuyerFavoriteResponse> list = buyerFavoriteRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId())
                .stream()
                .map(h-> h.toDto(shop(h.getShopId()),userInfoService.getUserInfoByUserIdFavorite(h.getUserId()) == null ? null : userInfoService.getUserInfoByUserIdFavorite(h.getUserId()).getProfileImage()))
                .collect(Collectors.toList());
        if(list.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.EMPTY_FAV_LIST);
        }
        return list;
    }

    @Override
    public Void removeShopFromFavoriteList(UUID id) {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        BuyerFavorite buyer = buyerFavoriteRepository.findByShopIdAndOwnerId(id, createdBy(UUID.fromString(currentUser())).getId());
        if(buyer == null){
            throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
        }
        buyerFavoriteRepository.deleteById(buyer.getId());
        return null;
    }

    @Override
    public BuyerFavoriteResponse getShopFromFavoriteList(UUID id) {
        String profileImage = null;
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        BuyerFavorite buyer = buyerFavoriteRepository.findByShopIdAndOwnerId(id, createdBy(UUID.fromString(currentUser())).getId());
        if(buyer != null){
            UserInfoResponse user = userInfoService.getUserInfoByUserIdFavorite(buyer.getUserId());
            if(user != null){
                profileImage = user.getProfileImage();
            }
            return buyer.toDto(shop(id),profileImage);
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND_IN_LIST);
    }

    // Return Shop
    public ShopResponse shop(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            try {
                return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                        .baseUrl("http://8.222.225.41:8088/")
                        .build()
                        .get()
                        .uri("api/v1/shops/{id}", id)
                        .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), ShopResponse.class);

            }catch (Exception e){
                throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
    }

    // Returning Token
    public String currentUser() {
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
        return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                .baseUrl("http://8.222.225.41:8081/")
                .build()
                .get()
                .uri("api/v1/users/{id}", id)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block()).getPayload(), User.class);
    }

    // Validation legal Role
    public void isLegal(UUID id){
        if(!createdBy(id).getLoggedAs().equalsIgnoreCase(String.valueOf(Role.BUYER))){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_PROCESS);
        }
    }

    // Account not yet verify
    public void isNotVerify(UUID id){
        UserRepresentation user = keycloak.realm(realm).users().get(String.valueOf(id)).toRepresentation();
        if(!user.getAttributes().get("is_verify").get(0).equalsIgnoreCase("true")){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_USER);
        }
    }

}
