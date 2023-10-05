package com.example.userservice.controller;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.userservice.request.UserRequest;
import com.example.userservice.service.User.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
public class AppUserController {

    private final UserService userService;

    public AppUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUser(){
        return new ResponseEntity<>(new ApiResponse<>(
                "User fetched successfully",
                userService.getAllUsers(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "User fetched by id successfully",
                userService.getUserById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/users/username")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<User>>> getAllUserByUsername(@RequestParam String username){
        return new ResponseEntity<>(new ApiResponse<>(
                "User search by username successfully",
                userService.findByUsername(username),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/users/email")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<List<User>>> getAllUserByEmail(@RequestParam String email){
        return new ResponseEntity<>(new ApiResponse<>(
                "User search by email successfully",
                userService.findByEmail(email),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<User>> postUser(@RequestBody UserRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "User posted successfully",
                userService.postUser(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{id}")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable UUID id){

        return new ResponseEntity<>(new ApiResponse<>(
                "User delete by id successfully",
                userService.deleteUser(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/users/{id}")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable UUID id,
                           @RequestBody UserRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "User updated by id successfully",
                userService.updateUser(id, request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }


}
