package com.example.productforsaleservice.service;

import com.example.productforsaleservice.request.FileRequest;
import com.example.productforsaleservice.request.ProductForSaleRequest;
import com.example.productforsaleservice.response.ProductForSaleResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface ProductForSaleService {
    List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException;

    ProductForSaleResponse addProductToPost(ProductForSaleRequest postRequest);

    List<ProductForSaleResponse> getAllProduct();

    ProductForSaleResponse getProductById(UUID id);

    ProductForSaleResponse deleteProductById(UUID id);

    ProductForSaleResponse updatePostById(UUID id, ProductForSaleRequest request);

    List<ProductForSaleResponse> getProductByPostId(UUID id);
}
