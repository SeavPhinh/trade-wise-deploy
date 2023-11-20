package com.example.manageuserservice.service.sellerfavorite;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.PostResponse;
import com.example.manageuserservice.exception.NotFoundExceptionClass;
import com.example.manageuserservice.model.SellerFavorite;
import com.example.manageuserservice.repository.SellerFavoriteRepository;
import com.example.manageuserservice.request.SellerFavoriteRequest;
import com.example.manageuserservice.response.SellerFavoriteResponse;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SellerFavoriteServiceImpl implements SellerFavoriteService {

    private final SellerFavoriteRepository sellerFavoriteRepository;
    private final UserInfoServiceImpl userInfoService;
    private final WebClient.Builder webClient;
    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public SellerFavoriteServiceImpl(SellerFavoriteRepository favoriteRepository, UserInfoServiceImpl userInfoService, WebClient.Builder webClient, Keycloak keycloak) {
        this.sellerFavoriteRepository = favoriteRepository;
        this.userInfoService = userInfoService;
        this.webClient = webClient;
        this.keycloak = keycloak;
    }

    @Override
    public SellerFavoriteResponse addedShopToFavoriteList(SellerFavoriteRequest request) {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        SellerFavorite buyerFav = sellerFavoriteRepository.findByUserIdAndPostId(request.getPostId(),createdBy(UUID.fromString(currentUser())).getId());
        if(buyerFav != null){
            throw new IllegalArgumentException(ValidationConfig.ALREADY_FAV_TO_POST);
        }
        PostResponse post = post(request.getPostId());
        return sellerFavoriteRepository.save(request.toEntity(createdBy(UUID.fromString(currentUser())).getId())).toDto(post);
    }

    @Override
    public List<SellerFavoriteResponse> getAllPostedFromSellerFavoriteListByOwnerId() {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        List<SellerFavoriteResponse> list = sellerFavoriteRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId()).stream().map(h-> h.toDto(post(h.getPostId()))).collect(Collectors.toList());
        if(list.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.EMPTY_FAV_LIST);
        }
        return list;
    }

    @Override
    public Void removePostedFromFavoriteList(UUID id) {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        SellerFavorite seller = sellerFavoriteRepository.findByPostIdAndOwnerId(id, createdBy(UUID.fromString(currentUser())).getId());
        if(seller != null){
            sellerFavoriteRepository.deleteById(seller.getId());
            return null;
        }
        throw new NotFoundExceptionClass(ValidationConfig.POST_NOTFOUND);
    }

    @Override
    public SellerFavoriteResponse getPostedFromFavoriteList(UUID id) {
        isNotVerify(UUID.fromString(currentUser()));
        isLegal(UUID.fromString(currentUser()));
        SellerFavorite seller = sellerFavoriteRepository.findByPostIdAndOwnerId(id, createdBy(UUID.fromString(currentUser())).getId());
        if(seller != null){
            return seller.toDto(post(id));
        }
        throw new NotFoundExceptionClass(ValidationConfig.POST_NOTFOUND_IN_LIST);
    }

    // Return Shop
    public PostResponse post(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt){
            try {
                return covertSpecificClass.convertValue(Objects.requireNonNull(webClient
                        .baseUrl("http://8.222.225.41:8083/")
                        .build()
                        .get()
                        .uri("api/v1/posts/{id}", id)
                        .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(), PostResponse.class);
            }catch (Exception e){
                throw new NotFoundExceptionClass(ValidationConfig.POST_NOTFOUND);
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.POST_NOTFOUND);
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

    // Validation legal Role
    public void isLegal(UUID id){
        if(!createdBy(id).getLoggedAs().equalsIgnoreCase(String.valueOf(Role.SELLER))){
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
