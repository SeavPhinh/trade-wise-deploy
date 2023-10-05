package com.example.userservice.controller;
import com.example.commonservice.model.User;
import com.example.userservice.request.UserRequest;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    public List<User> getAllUser(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable UUID id){
        return userService.getUserById(id);
    }

    @GetMapping("/users/username")
    @SecurityRequirement(name = "oAuth2")
    public List<User> getAllUserByUsername(@RequestParam String username){
        return userService.findByUsername(username);
    }

    @GetMapping("/users/email")
    @SecurityRequirement(name = "oAuth2")
    public List<User> getAllUserByEmail(@RequestParam String email){
        return userService.findByEmail(email);
    }

    @PostMapping("/users")
    public User postUser(@RequestBody UserRequest request){
        return userService.postUser(request);
    }

    @DeleteMapping("/users/{id}")
    @SecurityRequirement(name = "oAuth2")
    public String deleteUser(@PathVariable UUID id){
        userService.deleteUser(id);
        return "User Deleted Successfully.";
    }

    @PutMapping("/users/{id}")
    @SecurityRequirement(name = "oAuth2")
    public User updateUser(@PathVariable UUID id,
                           @RequestBody UserRequest request){
        return userService.updateUser(id, request);
    }


}
