package com.example.manageuserservice.service.userinfo;

import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserInfoService {
    UserInfoResponse saveFile(MultipartFile file, HttpServletRequest request) throws IOException;

    UserInfoResponse addUserDetail(UserInfoRequest request) throws Exception;

    UserInfoResponse getUserInfoByUserId(UUID id);

    UserInfoResponse getCurrentUserInfo();

    UserInfoResponse updateCurrentUserInfo(UserInfoRequest request);

    ByteArrayResource getImage(String fileName) throws IOException;
}
