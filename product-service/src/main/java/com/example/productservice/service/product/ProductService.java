package com.example.productservice.service.product;

import com.example.productservice.request.FileRequest;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ProductResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface ProductService {

    List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException;

    ProductResponse addProduct(ProductRequest postRequest);

    List<ProductResponse> getAllProduct();

    ProductResponse getProductById(UUID id);

    ProductResponse deleteProductById(UUID id);

    ProductResponse updateProductById(UUID id, ProductRequest request);

    List<ProductResponse> getAllProductByShopId(UUID id);
}
