package com.example.userservice.service.User;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.userservice.exception.NotFoundExceptionClass;
import com.example.userservice.model.UserDto;
import com.example.userservice.model.UserLogin;
import com.example.userservice.model.UserResponse;
import com.example.userservice.model.VerifyLogin;
import com.example.userservice.request.ChangePassword;
import com.example.userservice.request.RequestResetPassword;
import com.example.userservice.request.ResetPassword;
import com.example.userservice.request.UserRequest;
import com.example.userservice.service.Mail.EmailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final Keycloak keycloak;
    private final EmailService emailService;

    @Value("${keycloak.credentials.secret}")
    private String secretKey;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.auth-server-url}")
    private String authUrl;
    @Value("${keycloak.realm}")
    private String realm;

    public UserServiceImpl(Keycloak keycloak, EmailService emailService) {
        this.keycloak = keycloak;
        this.emailService = emailService;
    }

    public List<User> getAllUsers() {

        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        if(userRepresentations.stream().toList().isEmpty()){
            throw new UsernameNotFoundException("User not found.");
        }

        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public List<User> findByUsername(String username) {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().search(username);

        if(userRepresentations.stream().toList().isEmpty()){
            throw new NotFoundExceptionClass("User not found.");
        }

        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public User postUser(UserRequest request) {

        UsersResource usersResource = keycloak.realm(realm).users();
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(request.getPassword());

        UserRepresentation userPre = new UserRepresentation();
        userPre.setUsername(request.getUsername());
        userPre.setCredentials(Collections.singletonList(credentialRepresentation));
        userPre.setFirstName(request.getFirstname());
        userPre.setLastName(request.getLastname());
        userPre.setEmail(request.getEmail());
        userPre.singleAttribute("role", String.valueOf(request.getRoles()));
        userPre.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        userPre.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userPre.setEnabled(true);
        usersResource.create(userPre);

        UserRepresentation createdUserRepresentation =
                keycloak.realm(realm).users().search(request.getUsername()).get(0);

        return new User(
                UUID.fromString(createdUserRepresentation.getId()),
                createdUserRepresentation.getUsername(),
                createdUserRepresentation.getEmail(),
                createdUserRepresentation.getFirstName(),
                createdUserRepresentation.getLastName(),
                roles(userPre.getAttributes().get("role").get(0)),
                LocalDateTime.parse(userPre.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(userPre.getAttributes().get("lastModified").get(0))
        );
    }

    public User deleteUser(UUID userId){
        User responseUser = new User();
        responseUser.setId(UUID.fromString(resource(userId).toRepresentation().getId()));
        responseUser.setUsername(resource(userId).toRepresentation().getUsername());
        responseUser.setEmail(resource(userId).toRepresentation().getUsername());
        responseUser.setFirstName(resource(userId).toRepresentation().getFirstName());
        responseUser.setLastName(resource(userId).toRepresentation().getLastName());
        responseUser.setRoles(roles(resource(userId).toRepresentation().getAttributes().get("role").get(0)));
        responseUser.setCreatedDate(LocalDateTime.now());
        responseUser.setLastModified(LocalDateTime.now());
        keycloak.realm(realm).users().delete(String.valueOf(userId));
        return responseUser;
    }

    public User updateUser(UUID id, UserRequest request) {

        UserRepresentation updatedUser = new UserRepresentation();

        updatedUser.setUsername(request.getUsername());
        updatedUser.setFirstName(request.getFirstname());
        updatedUser.setLastName(request.getLastname());
        updatedUser.setEmail(request.getEmail());
        updatedUser.singleAttribute("role", String.valueOf(request.getRoles()));
        updatedUser.singleAttribute("createdDate", String.valueOf(resource(id).toRepresentation().getAttributes().get("createdDate").get(0)));
        updatedUser.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        resource(id).update(updatedUser);

        UserRepresentation updatedUserRepresentation = resource(id).toRepresentation();

        return new User(
                UUID.fromString(updatedUserRepresentation.getId()),
                updatedUserRepresentation.getUsername(),
                updatedUserRepresentation.getEmail(),
                updatedUserRepresentation.getFirstName(),
                updatedUserRepresentation.getLastName(),
                roles(updatedUser.getAttributes().get("role").get(0)),
                LocalDateTime.parse(updatedUser.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(updatedUser.getAttributes().get("lastModified").get(0))
        );
    }

    @Override
    public User changePassword(UUID id, ChangePassword request) {

        CredentialRepresentation passwordCredential = new CredentialRepresentation();
        passwordCredential.setType(CredentialRepresentation.PASSWORD);
        passwordCredential.setValue(request.getNewPassword());
        passwordCredential.setTemporary(false);
        resource(id).resetPassword(passwordCredential);

        return new User(
                UUID.fromString(resource(id).toRepresentation().getId()),
                resource(id).toRepresentation().getUsername(),
                resource(id).toRepresentation().getEmail(),
                resource(id).toRepresentation().getFirstName(),
                resource(id).toRepresentation().getLastName(),
                roles(resource(id).toRepresentation().getAttributes().get("role").get(0)),
                LocalDateTime.parse(resource(id).toRepresentation().getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(resource(id).toRepresentation().getAttributes().get("lastModified").get(0))
        );
    }

    @Override
    public User loginAccount(UserLogin login) throws MessagingException {
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = login.getAccount();
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                setAttribute(user, login);
                return returnUser(user);
            }
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public UserResponse verifiedAccount(VerifyLogin login){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = login.getAccount();
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                Map<String, List<String>> attributes = user.getAttributes();

                if(attributes == null || !attributes.containsKey("otpCode")){
                    throw new UsernameNotFoundException("User not found");

                }else if(user.getAttributes().get("otpCode").get(0).equalsIgnoreCase(login.getOtpCode())){
                    return new UserResponse(
                            UUID.fromString(resource(UUID.fromString(user.getId())).toRepresentation().getId()),
                            resource(UUID.fromString(user.getId())).toRepresentation().getUsername(),
                            resource(UUID.fromString(user.getId())).toRepresentation().getEmail(),
                            resource(UUID.fromString(user.getId())).toRepresentation().getFirstName(),
                            resource(UUID.fromString(user.getId())).toRepresentation().getLastName(),
                            roles(resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("role").get(0)),
                            accessTokenResponse(login),
                            LocalDateTime.parse(user.getAttributes().get("createdDate").get(0)),
                            LocalDateTime.parse(user.getAttributes().get("lastModified").get(0))
                    );
                }else{
                    throw new IllegalArgumentException("Incorrect otpCode.");
                }
            }
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public User resetPassword(ResetPassword change){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = change.getAccount();

            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                Map<String, List<String>> attributes = user.getAttributes();
                if(attributes == null || !attributes.containsKey("otpCode")){
                    throw new UsernameNotFoundException("User not found");
                }else if(user.getAttributes().get("otpCode").get(0).equalsIgnoreCase(change.getOtpCode())){
                    CredentialRepresentation passwordCredential = new CredentialRepresentation();
                    passwordCredential.setType(CredentialRepresentation.PASSWORD);
                    passwordCredential.setValue(change.getNewPassword());
                    passwordCredential.setTemporary(false);
                    resource(UUID.fromString(user.getId())).resetPassword(passwordCredential);
                    return returnUser(user);
                }else{
                    throw new IllegalArgumentException("Incorrect otpCode.");
                }
            }
        }
        throw new UsernameNotFoundException("User not found");
    }

    @Override
    public RequestResetPassword sendOptCode(RequestResetPassword reset) throws MessagingException {
        User account = findByEmail(new RequestResetPassword(reset.getAccount()));
        if (account.getEmail().equalsIgnoreCase(reset.getAccount()) || account.getUsername().equalsIgnoreCase(reset.getAccount())) {
            emailService.resetPassword(reset.getAccount());
            return reset;
        }
        throw new UsernameNotFoundException("User not found.");
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public User findByEmail(RequestResetPassword email) {

        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        for (UserRepresentation user : userRepresentations) {
            if(user.getEmail().equalsIgnoreCase(email.getAccount())){
                return returnUser(user);
            }
        }
        throw new UsernameNotFoundException("User not found");
    }

    public User getUserById(UUID id) {
        return new User(UUID.fromString(resource(id).toRepresentation().getId()),
                resource(id).toRepresentation().getUsername(),
                resource(id).toRepresentation().getEmail(),
                resource(id).toRepresentation().getFirstName(),
                resource(id).toRepresentation().getLastName(),
                roles(resource(id).toRepresentation().getAttributes().get("role").get(0)),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    // Converting Role from Attribute as String to ArrayList
    public List<Role> roles(String role){
        List<String> rolesList = Arrays.asList(role.replaceAll("\\[|\\]", "").split(", "));
        return rolesList.stream()
                .map(Role::valueOf)
                .collect(Collectors.toList());
    }

    // Returning UserResource by id
    public UserResource resource(UUID id){
        return keycloak.realm(realm).users().get(String.valueOf(id));
    }

    // Validating Account
    public String accessTokenResponse(VerifyLogin login){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(login.getAccount())){
               return myKeyCloak(user.getUsername(),login.getPassword());
            }}
        return myKeyCloak(login.getAccount(),login.getPassword());
    }

    // Returning Access Token
    public String myKeyCloak(String username, String password){
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(authUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(secretKey)
                .username(username)
                .password(password)
                .build();
        AccessTokenResponse tok = keycloak.tokenManager().getAccessToken();
        return tok.getToken();
    }

    // Return User Object
    public User returnUser(UserRepresentation user){
        return new User(
                UUID.fromString(resource(UUID.fromString(user.getId())).toRepresentation().getId()),
                resource(UUID.fromString(user.getId())).toRepresentation().getUsername(),
                resource(UUID.fromString(user.getId())).toRepresentation().getEmail(),
                resource(UUID.fromString(user.getId())).toRepresentation().getFirstName(),
                resource(UUID.fromString(user.getId())).toRepresentation().getLastName(),
                roles(resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("role").get(0)),
                LocalDateTime.parse(user.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(user.getAttributes().get("lastModified").get(0))
        );
    }

    // Set otp attribute for user
    public void setAttribute (UserRepresentation user, UserLogin login) throws MessagingException {
        user.singleAttribute("otpCode",String.valueOf(emailService.verifyCode(login.getAccount())));
        resource(UUID.fromString(user.getId())).update(user);
    }


    // Validating password ( Min 6 chars Max 8 chars one - uppercase letter, lowercase letter, number )
    public static boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 8) {
            return false;
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }

}
