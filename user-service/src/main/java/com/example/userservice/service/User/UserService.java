package com.example.userservice.service.User;


import com.example.commonservice.model.User;
import com.example.userservice.model.UserLogin;
import com.example.userservice.model.UserResponse;
import com.example.userservice.model.VerifyLogin;
import com.example.userservice.request.*;
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

    User updateUser(UUID id, UserUpdate request);

    User changePassword(UUID id, ChangePassword request);

    User loginAccount(UserLogin login) throws MessagingException;

    UserResponse verifiedAccount(VerifyLogin login) throws MessagingException;

    User resetPassword(ResetPassword change) throws MessagingException;

    RequestResetPassword sendOptCode(RequestResetPassword reset) throws MessagingException;
}
