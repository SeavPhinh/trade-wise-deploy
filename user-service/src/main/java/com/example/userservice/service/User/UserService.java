package com.example.userservice.service.User;


import com.example.commonservice.model.User;
import com.example.userservice.model.UserLogin;
import com.example.userservice.model.UserResponse;
import com.example.userservice.request.ChangePassword;
import com.example.userservice.request.UserRequest;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    
    List<User> getAllUsers();

    User getUserById(UUID id);

    List<User> findByUsername(String username);

    List<User> findByEmail(String email);

    User postUser(UserRequest request);

    User deleteUser(UUID id);

    User updateUser(UUID id, UserRequest request);

    User changePassword(UUID id, ChangePassword request);

    UserResponse loginAccount(UserLogin login) throws MessagingException;
}
