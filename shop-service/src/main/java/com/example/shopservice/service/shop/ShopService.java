package com.example.shopservice.service.shop;

import com.example.commonservice.response.FileResponse;
import com.example.shopservice.request.FileRequest;
import com.example.shopservice.request.ShopRequest;
import com.example.shopservice.response.ShopResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface ShopService {

    ShopResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException;

    ShopResponse setUpShop(ShopRequest request) throws Exception;

    List<ShopResponse> getAllShop();

    ShopResponse getShopById(UUID id);

    ShopResponse updateShopById(ShopRequest request);

    ShopResponse getShopByOwnerId();

    ShopResponse shopAction(Boolean isActive);
    ByteArrayResource getImage(String fileName) throws IOException;
}
