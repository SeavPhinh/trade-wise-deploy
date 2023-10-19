package com.example.manageuserservice.service.buyerfavorite;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.model.Shop;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.manageuserservice.exception.NotFoundExceptionClass;
import com.example.manageuserservice.model.BuyerFavorite;
import com.example.manageuserservice.repository.BuyerFavoriteRepository;
import com.example.manageuserservice.request.BuyerFavoriteRequest;
import com.example.manageuserservice.response.BuyerFavoriteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
public class BuyerFavoriteServiceImpl implements BuyerFavoriteService {

    private final BuyerFavoriteRepository buyerFavoriteRepository;
    private final WebClient shopWeb;
    private final WebClient userWeb;

    public BuyerFavoriteServiceImpl(BuyerFavoriteRepository buyerFavoriteRepository, WebClient.Builder shopWeb, WebClient.Builder userWeb) {
        this.buyerFavoriteRepository = buyerFavoriteRepository;
        this.shopWeb = shopWeb.baseUrl("http://localhost:8088/").build();
        this.userWeb = userWeb.baseUrl("http://localhost:8081/").build();
    }

    @Override
    public BuyerFavoriteResponse addedShopToFavoriteList(BuyerFavoriteRequest request) {
        BuyerFavorite buyerFav = buyerFavoriteRepository.findByUserIdAndShopId(request.getShopId(),createdBy(UUID.fromString(currentUser())).getId());
        if(buyerFav != null){
            throw new IllegalArgumentException(ValidationConfig.ALREADY_FAV_TO_SHOP);
        }
        Shop shop = shop(request.getShopId());
        return buyerFavoriteRepository.save(request.toEntity(createdBy(UUID.fromString(currentUser())).getId())).toDto(shop);
    }

    @Override
    public List<BuyerFavoriteResponse> getCurrentUserInfo() {
        List<BuyerFavoriteResponse> list = buyerFavoriteRepository.findByOwnerId(createdBy(UUID.fromString(currentUser())).getId()).stream().map(h-> h.toDto(shop(h.getShopId()))).collect(Collectors.toList());
        if(list.isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.EMPTY_FAV_LIST);
        }
        return list;
    }

    @Override
    public BuyerFavoriteResponse removeShopFromFavoriteList(UUID id) {
        BuyerFavoriteResponse delete = getShopFromFavoriteList(id);
        buyerFavoriteRepository.deleteById(delete.getId());
        return delete;
    }

    @Override
    public BuyerFavoriteResponse getShopFromFavoriteList(UUID id) {
        BuyerFavorite buyer = buyerFavoriteRepository.findByShopIdAndOwnerId(id, createdBy(UUID.fromString(currentUser())).getId());
        if(buyer != null){
            return buyer.toDto(shop(id));
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND_IN_LIST);
    }

    // Return Shop
    public Shop shop(UUID id){
        ObjectMapper covertSpecificClass = new ObjectMapper();
        covertSpecificClass.registerModule(new JavaTimeModule());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt){
            try {
                return covertSpecificClass.convertValue(Objects.requireNonNull(shopWeb
                        .get()
                        .uri("api/v1/shops/{id}", id)
                        .headers(h -> h.setBearerAuth(jwt.getTokenValue()))
                        .retrieve()
                        .bodyToMono(ApiResponse.class)
                        .block()).getPayload(),Shop.class);
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
        return covertSpecificClass.convertValue(Objects.requireNonNull(userWeb
                .get()
                .uri("api/v1/users/{id}", id)
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .block()).getPayload(), User.class);
    }



}
