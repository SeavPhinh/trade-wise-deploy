package com.example.manageuserservice.controller;

import com.example.commonservice.response.ApiResponse;
import com.example.manageuserservice.exception.NotFoundExceptionClass;
import com.example.manageuserservice.request.FileRequest;
import com.example.manageuserservice.request.UserInfoRequest;
import com.example.manageuserservice.response.FileResponse;
import com.example.manageuserservice.response.UserInfoResponse;
import com.example.manageuserservice.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@Tag(name = "User Info")
@SecurityRequirement(name = "oAuth2")
public class UserInfoController {

    private final UserInfoService userInfoService;

    public UserInfoController(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @PostMapping(value = "/user-info")
    @Operation(summary = "user adding user information")
    public ResponseEntity<ApiResponse<UserInfoResponse>> addUserDetail(@Valid @RequestBody UserInfoRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "user has added information successfully",
                userInfoService.addUserDetail(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @GetMapping("/user-info/{id}")
    @Operation(summary = "fetched user information by owner id")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getUserInfoByOwnerId(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "user information fetched by id successfully",
                userInfoService.getUserInfoByOwnerId(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/user-info/current")
    @Operation(summary = "fetched current user information")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUserInfo(){
        return new ResponseEntity<>(new ApiResponse<>(
                "fetch current user successfully",
                userInfoService.getCurrentUserInfo(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/user-info/current")
    @Operation(summary = "update current user's information")
    public ResponseEntity<ApiResponse<UserInfoResponse>> updateCurrentUserInfo( @Valid @RequestBody UserInfoRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                " updated current user information successfully",
                userInfoService.updateCurrentUserInfo(request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @PostMapping(value = "/file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "upload file")
    public ResponseEntity<ApiResponse<FileResponse>> saveFile(@RequestParam(required = false) MultipartFile file,
                                                              HttpServletRequest request) throws IOException {
        return new ResponseEntity<>(new ApiResponse<>(
                "image upload to user information successfully",
                userInfoService.saveFile(file,request),
                HttpStatus.OK
        ), HttpStatus.OK);

    }





}
