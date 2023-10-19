package com.example.shopservice.service.rating;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.shopservice.exception.NotFoundExceptionClass;
import com.example.shopservice.exception.NullExceptionClass;
import com.example.shopservice.model.Rating;
import com.example.shopservice.model.Shop;
import com.example.shopservice.repository.RatingRepository;
import com.example.shopservice.repository.ShopRepository;
import com.example.shopservice.request.RatingRequest;
import com.example.shopservice.response.RatingResponse;
import com.example.shopservice.response.ShopResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final ShopRepository service;
    private final WebClient webClient;

    public RatingServiceImpl(RatingRepository ratingRepository, ShopRepository service, WebClient.Builder webClient) {
        this.ratingRepository = ratingRepository;
        this.service = service;
        this.webClient = webClient.baseUrl("http://localhost:8081/").build();
    }

    @Override
    public RatingResponse ratingShop(RatingRequest request) {
        if(request.getLevel() == null || request.getShopId() == null){
            throw new NullExceptionClass(ValidationConfig.NULL_FIELD);
        }
        Optional<Shop> shop = service.findById(request.getShopId());
        if(shop.isPresent()){
            if(!shop.get().getStatus()){
                throw new IllegalArgumentException(ValidationConfig.INACTIVE_SHOP);
            }
            Rating rating = ratingRepository.getRatingRecordByOwnerId(createdBy(UUID.fromString(currentUser())).getId());
            if(rating != null){
                rating.setLevel(request.getLevel());
                return ratingRepository.save(rating).toDto(request.getShopId());
            }
            return ratingRepository.save(request.toEntity(createdBy(UUID.fromString(currentUser())).getId(),service.findById(request.getShopId()).orElseThrow())).toDto(request.getShopId());
        }
        throw new NotFoundExceptionClass(ValidationConfig.SHOP_NOTFOUND);
    }

    @Override
    public List<ShopResponse> getRatedShopByCurrentId() {
        List<Rating> ratings = ratingRepository.findAll();
        if(!ratings.isEmpty()){
            List<ShopResponse> shops = new ArrayList<>();
            for (Rating rate : ratings) {
                Shop shop = service.getActiveShopById(rate.getShop().getId());
                if(shop != null){
                    shops.add(shop.toDto());
                }
            }
            if(!shops.isEmpty()){
                return shops;
            }
            throw new NotFoundExceptionClass(ValidationConfig.NOT_RATING);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOT_RATING);
    }

    // Returning Token
    public String currentUser(){
        try{
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

}
