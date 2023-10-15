package com.example.shopservice.service.shop;

import com.example.shopservice.request.FileRequest;
import com.example.shopservice.request.ShopRequest;
import com.example.shopservice.response.ShopResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface ShopService {

    public List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException;

    ShopResponse setUpShop(ShopRequest request);

    List<ShopResponse> getAllShop();

    ShopResponse getShopById(UUID id);

    ShopResponse deleteShopById(UUID id);

    ShopResponse updateShopById(UUID id, ShopRequest request);
}
