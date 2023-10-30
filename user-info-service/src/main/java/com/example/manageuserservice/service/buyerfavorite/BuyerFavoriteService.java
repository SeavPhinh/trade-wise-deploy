package com.example.manageuserservice.service.buyerfavorite;

import com.example.commonservice.enumeration.Role;
import com.example.manageuserservice.request.BuyerFavoriteRequest;
import com.example.manageuserservice.response.BuyerFavoriteResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface BuyerFavoriteService {
    BuyerFavoriteResponse addedShopToFavoriteList(BuyerFavoriteRequest request);

    List<BuyerFavoriteResponse> getCurrentUserInfo();

    Void removeShopFromFavoriteList(UUID id);

    BuyerFavoriteResponse getShopFromFavoriteList(UUID id);
}
