package com.example.manageuserservice.service.sellerfavorite;

import com.example.manageuserservice.request.SellerFavoriteRequest;
import com.example.manageuserservice.response.SellerFavoriteResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface SellerFavoriteService {

    SellerFavoriteResponse getPostedFromFavoriteList(UUID id);

    List<SellerFavoriteResponse> getAllPostedFromSellerFavoriteListByOwnerId();

    Void removePostedFromFavoriteList(UUID id);

    SellerFavoriteResponse addedShopToFavoriteList(SellerFavoriteRequest request);
}
