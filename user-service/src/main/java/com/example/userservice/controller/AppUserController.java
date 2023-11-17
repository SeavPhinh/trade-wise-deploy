package com.example.userservice.controller;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.userservice.model.UserCreated;
import com.example.userservice.model.UserLogin;
import com.example.userservice.model.UserResponse;
import com.example.userservice.model.VerifyLogin;
import com.example.userservice.request.*;
import com.example.userservice.service.User.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
@RequestMapping("api/v1/users")
@Tag(name = "AppUser")
@CrossOrigin
public class AppUserController {

    private final UserService userService;

    public AppUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    @Operation(summary = "fetch all user from keycloak")
    public ResponseEntity<ApiResponse<List<User>>> getAllUser(){
        return new ResponseEntity<>(new ApiResponse<>(
                "User fetched successfully",
                userService.getAllUsers(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "fetch user by id from keycloak")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "User fetched by id successfully",
                userService.getUserById(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/current")
    @Operation(summary = "*fetch current from keycloak")
    @SecurityRequirement(name = "oAuth2")
    public ResponseEntity<ApiResponse<User>> getCurrentUser(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Current User fetched successfully",
                userService.getCurrentUser(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/username")
    @Operation(summary = "fetch user by username from keycloak")
    public ResponseEntity<ApiResponse<List<User>>> getAllUserByUsername(@RequestParam String username){
        return new ResponseEntity<>(new ApiResponse<>(
                "User search by username successfully",
                userService.findByUsername(username),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @GetMapping("/email")
    @Operation(summary = "fetch user by email from keycloak")
    public ResponseEntity<ApiResponse<List<User>>> getAllUserByEmail(@RequestParam String email){
        return new ResponseEntity<>(new ApiResponse<>(
                "User search by email successfully",
                userService.findByEmail(email),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("")
    @Operation(summary = "register user to keycloak")
    public ResponseEntity<ApiResponse<UserCreated>> addingUser(@Valid @RequestBody UserRequest request) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "User posted successfully",
                userService.postUser(request),
                HttpStatus.CREATED
        ), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "oAuth2")
    @Operation(summary = "delete user by id from keycloak")
    public ResponseEntity<ApiResponse<Void>> deleteUserById(@PathVariable UUID id){
        return new ResponseEntity<>(new ApiResponse<>(
                "User delete by id successfully",
                userService.deleteUser(id),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/verify")
    @Operation(summary = "verified account first login")
    public ResponseEntity<ApiResponse<UserResponse>> verify(@RequestParam(defaultValue = "BUYER") Role role, @Valid @RequestBody VerifyLogin login) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "Account verified successfully",
                userService.verifiedAccount(role,login),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/login")
    @Operation(summary = "*logging account")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestParam(defaultValue = "BUYER") Role role, @Valid @RequestBody UserLogin login){
        return new ResponseEntity<>(new ApiResponse<>(
                "Account logged successfully",
                userService.loginAccount(role,login),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PutMapping("/reset-password")
    @Operation(summary = "Reset user password or change password")
    public ResponseEntity<ApiResponse<User>> resetPassword(@Valid @RequestBody ResetPassword change) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "Password reset successfully",
                userService.resetPassword(change),
                HttpStatus.OK
        ), HttpStatus.OK);
    }

    @PostMapping("/otp/reset-password")
    @Operation(summary = "Sending otpCode to user")
    public ResponseEntity<ApiResponse<RequestResetPassword>> otpResetPassword(@Valid @RequestBody RequestResetPassword reset) throws MessagingException {
        return new ResponseEntity<>(new ApiResponse<>(
                "OtpCode sent successfully",
                userService.sendOptCode(reset),
                HttpStatus.OK
        ), HttpStatus.OK);
    }



}
