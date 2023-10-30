package com.example.productservice.service.comment;

import com.example.productservice.request.FileRequest;
import com.example.productservice.request.ProductForSaleRequest;
import com.example.productservice.request.ProductForSaleRequestUpdate;
import com.example.productservice.response.ProductForSaleResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public interface ProductForSaleService {
    ProductForSaleResponse saveListFile(UUID id,List<MultipartFile> files, HttpServletRequest request) throws IOException;

    ProductForSaleResponse addProductToPost(ProductForSaleRequest postRequest) throws Exception;

    List<ProductForSaleResponse> getAllProduct();

    ProductForSaleResponse getProductById(UUID id);

    String deleteProductById(UUID id);

    ProductForSaleResponse updateProductById(UUID id, ProductForSaleRequestUpdate request) throws Exception;

    List<ProductForSaleResponse> getProductByPostId(UUID id);

    ByteArrayResource getImage(String fileName) throws IOException;
}
