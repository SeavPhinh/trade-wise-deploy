package com.example.productservice.service;

import com.example.productservice.request.FileRequest;
import com.example.productservice.request.ProductRequest;
import com.example.productservice.response.ProductResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductService {

    List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException;

    ProductResponse addProduct(ProductRequest postRequest);
}
