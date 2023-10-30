package com.example.manageuserservice.service.userinfo;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.response.FileResponse;
import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.request.UserInfoRequestUpdate;
import com.example.manageuserservice.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public interface UserInfoService {
    FileResponse saveFile(MultipartFile file, HttpServletRequest request) throws Exception;

    UserInfoResponse addUserDetail(UserInfoRequest request) throws Exception;

    UserInfoResponse getUserInfoByUserId(UUID id);

    UserInfoResponse getCurrentUserInfo();

    UserInfoResponse updateCurrentUserInfo(UserInfoRequestUpdate request) throws Exception;

    ByteArrayResource getImage(String fileName) throws IOException;

    Void switchRole(Role role);
}
