package com.example.shopservice.service;

import com.example.shopservice.request.FileRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface ShopService {

    public List<FileRequest> saveListFile(List<MultipartFile> files, HttpServletRequest request) throws IOException;
}
