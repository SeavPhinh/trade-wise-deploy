package com.example.manageuserservice.service;

import com.example.manageuserservice.request.FileRequest;
import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.response.FileResponse;
import com.example.manageuserservice.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserInfoService {
    FileResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException;

    UserInfoResponse addUserDetail(UserInfoRequest request);

    UserInfoResponse getUserInfoByOwnerId(UUID id);

    UserInfoResponse getCurrentUserInfo();

    UserInfoResponse updateCurrentUserInfo(UserInfoRequest request);
}
