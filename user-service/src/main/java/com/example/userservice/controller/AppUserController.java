package com.example.userservice.controller;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.userservice.model.UserLogin;
import com.example.userservice.model.UserResponse;
import com.example.userservice.model.VerifyLogin;
import com.example.userservice.request.ChangePassword;
import com.example.userservice.request.RequestResetPassword;
import com.example.userservice.request.ResetPassword;
import com.example.userservice.request.UserRequest;
import com.example.userservice.service.User.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@Tag(name = "AppUser")
public class AppUserController {

    private final UserService userService;

    public AppUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @Operation(summary = "fetch all user from keycloak")
    public ResponseEntity<ApiResponse<List<User>>> getAllUser(){
        return new ResponseEntity<>(new ApiResponse<>(
                "User fetched successfully",
                userService.getAllUsers(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "fetch user by id from keycloak")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "User fetched by id successfully",
                userService.getUserById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/users/username")
    @Operation(summary = "fetch user by username from keycloak")
    public ResponseEntity<ApiResponse<List<User>>> getAllUserByUsername(@RequestParam String username){
        return new ResponseEntity<>(new ApiResponse<>(
                "User search by username successfully",
                userService.findByUsername(username),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/users/email")
    @Operation(summary = "fetch user by email from keycloak")
    public ResponseEntity<ApiResponse<List<User>>> getAllUserByEmail(@RequestParam String email){
        return new ResponseEntity<>(new ApiResponse<>(
                "User search by email successfully",
                userService.findByEmail(email),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/users")
    @Operation(summary = "register user to keycloak")
    public ResponseEntity<ApiResponse<User>> postUser(@Valid @RequestBody UserRequest request){
        return new ResponseEntity<>(new ApiResponse<>(
                "User posted successfully",
                userService.postUser(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{id}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "delete user by id from keycloak")
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

    @PutMapping("/users/{id}/change-password")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<User>> changePassword(@PathVariable UUID id,
                                                        @RequestBody ChangePassword request){
        return new ResponseEntity<>(new ApiResponse<>(
                "Password changed successfully",
                userService.changePassword(id, request),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<User>> login(@RequestBody UserLogin login) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "Account logged successfully",
                userService.loginAccount(login),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<UserResponse>> verify(@RequestBody VerifyLogin login) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "Account verified successfully",
                userService.verifiedAccount(login),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ApiResponse<User>> resetPassword(@RequestBody ResetPassword change) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "Password reset successfully",
                userService.resetPassword(change),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }

    @PostMapping("/otp-reset-password")
    public ResponseEntity<ApiResponse<RequestResetPassword>> otpResetPassword(@RequestBody RequestResetPassword reset) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "OtpCode sent successfully",
                userService.sendOptCode(reset),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }






}
