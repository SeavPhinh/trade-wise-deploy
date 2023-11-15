package com.example.manageuserservice.controller;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.response.ApiResponse;
import com.example.commonservice.response.FileResponse;
import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.request.UserInfoRequestUpdate;
import com.example.manageuserservice.response.UserInfoResponse;
import com.example.manageuserservice.service.userinfo.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/user-info")
@Tag(name = "User Info")
@CrossOrigin
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping(value = "")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "user adding user information")
    public ResponseEntity<ApiResponse<UserInfoResponse>> addUserDetail(@Valid @RequestBody UserInfoRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                "user has added information successfully",
                userInfoService.addUserDetail(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "fetched user information by user id")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfoByUserId(@PathVariable UUID userId){
        return new ResponseEntity<>(new ApiResponse<>(
                "user information fetched by id successfully",
                userInfoService.getUserInfoByUserId(userId),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/current")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "fetched current user information")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUserInfo(){
        return new ResponseEntity<>(new ApiResponse<>(
                "fetch current user successfully",
                userInfoService.getCurrentUserInfo(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/current")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "update current user's information")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateCurrentUserInfo(@Valid @RequestBody UserInfoRequestUpdate request) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                " updated current user information successfully",
                userInfoService.updateCurrentUserInfo(request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/current/role")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "user switch role")
    public ResponseEntity<ApiResponse<Void>> switchRole(@RequestParam(defaultValue = "BUYER")Role role){
        return new ResponseEntity<>(new ApiResponse<>(
                "user switch role successfully",
                userInfoService.switchRole(role),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "upload profile image")
    public ResponseEntity<ApiResponse<FileResponse>> saveFile(@RequestParam(required = false) MultipartFile file,
                                                              HttpServletRequest request) throws Exception {
        return new ResponseEntity<>(new ApiResponse<>(
                "image upload to user information successfully",
                userInfoService.saveFile(file,request),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/image")
    @Operation(summary = "fetched image")
    public ResponseEntity<ByteArrayResource> getFileByFileName(@RequestParam String fileName) throws IOException {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(userInfoService.getImage(fileName));
    }

}
