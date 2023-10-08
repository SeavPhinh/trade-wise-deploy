package com.example.userservice.service.User;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.userservice.exception.NotFoundExceptionClass;
import com.example.userservice.model.UserDto;
import com.example.userservice.model.UserLogin;
import com.example.userservice.model.UserResponse;
import com.example.userservice.model.VerifyLogin;
import com.example.userservice.request.*;
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
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
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
            throw new NotFoundExceptionClass("Waiting for user registration");
        }

        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public List<User> findByUsername(String username) {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().search(username.replaceAll("\\s+",""));

        if(userRepresentations.stream().toList().isEmpty()){
            throw new NotFoundExceptionClass("User not found.");
        }

        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public User postUser(UserRequest request) {

        UsersResource usersResource = keycloak.realm(realm).users();

        if(whiteSpace(request.getPassword())){
            throw new IllegalArgumentException("Password cannot be whitespace");
        }

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(request.getPassword());

        // Validation Existing Account
        existingAccount(request.getEmail(),request.getUsername().replaceAll("\\s+",""));

        UserRepresentation userPre = new UserRepresentation();
        userPre.setUsername(request.getUsername().toLowerCase().replaceAll("\\s+",""));
        userPre.setCredentials(Collections.singletonList(credentialRepresentation));
        userPre.setFirstName(request.getFirstname());
        userPre.setLastName(request.getLastname());
        userPre.setEmail(request.getEmail().toLowerCase());
        if (!request.getRoles().contains(Role.BUYER) && !request.getRoles().contains(Role.SELLER)) {
            throw new NotFoundExceptionClass("Role must include BUYER or SELLER");
        }
        userPre.singleAttribute("role", String.valueOf(roles(String.valueOf(request.getRoles()))));
        userPre.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
        userPre.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        userPre.setEnabled(true);
        usersResource.create(userPre);

        UserRepresentation createdUserRepresentation =
                keycloak.realm(realm).users().search(request.getUsername().replaceAll("\\s+","").toLowerCase()).get(0);

        return new User(
                UUID.fromString(createdUserRepresentation.getId()),
                createdUserRepresentation.getUsername().toLowerCase().replaceAll("\\s+",""),
                createdUserRepresentation.getEmail().toLowerCase(),
                createdUserRepresentation.getFirstName(),
                createdUserRepresentation.getLastName(),
                roles(userPre.getAttributes().get("role").get(0)),
                LocalDateTime.parse(userPre.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(userPre.getAttributes().get("lastModified").get(0))
        );
    }

    public User deleteUser(UUID userId) {
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            User responseUser;
            if (user.getId().equalsIgnoreCase(String.valueOf(getUserById(userId).getId()))) {
                responseUser = getUserById(userId);
                keycloak.realm(realm).users().delete(String.valueOf(userId));
                return responseUser;
            }
        }
        throw new NotFoundExceptionClass("User not found.");

    }

    public User updateUser(UUID id, UserUpdate request) {

        UserRepresentation updatedUser = new UserRepresentation();
        resource(id);
        updatedUser.setFirstName(request.getFirstname().replaceAll("\\s+",""));
        updatedUser.setLastName(request.getLastname().replaceAll("\\s+",""));
        updatedUser.setEmail(request.getEmail());
        updatedUser.singleAttribute("role", String.valueOf(request.getRoles()));
        updatedUser.singleAttribute("createdDate", String.valueOf(resource(id).toRepresentation().getAttributes().get("createdDate").get(0)));
        updatedUser.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        resource(id).update(updatedUser);

        return getUserById(id);
    }

    @Override
    public User loginAccount(UserLogin login) throws MessagingException {
//        if(whiteSpace(login.getPassword())){
//            throw new IllegalArgumentException("Password cannot be whitespace");
//        }
        if(myKeyCloak(login.getAccount(),login.getPassword()) == null){
            throw new IllegalArgumentException("Email/Username or password is incorrect");
        }
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = login.getAccount().replaceAll("\\s+","");
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                setAttribute(user, login.getAccount());
                return returnUser(user);
            } else if (!user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                throw new NotFoundExceptionClass("User not found.");
            }
        }
        throw new IllegalArgumentException("Incorrect password");
    }

    @Override
    public UserResponse verifiedAccount(VerifyLogin login){

        String token = "";

//        if(whiteSpace(login.getPassword())){
//            throw new IllegalArgumentException("Password cannot be whitespace");
//        }

        if(accessTokenResponse(login) == null){
            throw new IllegalArgumentException("Email/Username or password is incorrect");
        }
        token = accessTokenResponse(login);

        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = login.getAccount().replaceAll("\\s+","");
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                Map<String, List<String>> attributes = user.getAttributes();
                if(attributes == null){
                    throw new NotFoundExceptionClass("User not found.");
                } else if (!attributes.containsKey("otpCode")) {
                    throw new IllegalArgumentException("Sending otpCode is required");
                } else if(user.getAttributes().get("otpCode").get(0).equalsIgnoreCase(login.getOtpCode().replaceAll("\\s+",""))){
                    return new UserResponse(
                            UUID.fromString(resource(UUID.fromString(user.getId())).toRepresentation().getId()),
                            resource(UUID.fromString(user.getId())).toRepresentation().getUsername(),
                            resource(UUID.fromString(user.getId())).toRepresentation().getEmail(),
                            resource(UUID.fromString(user.getId())).toRepresentation().getFirstName(),
                            resource(UUID.fromString(user.getId())).toRepresentation().getLastName(),
                            roles(resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("role").get(0)),
                            token,
                            LocalDateTime.parse(user.getAttributes().get("createdDate").get(0)),
                            LocalDateTime.parse(user.getAttributes().get("lastModified").get(0))
                    );
                }else{
                    throw new IllegalArgumentException("Incorrect otpCode.");
                }
            }
        }
        throw new NotFoundExceptionClass("User not found.");
    }

    @Override
    public User resetPassword(ResetPassword change){

        if(whiteSpace(change.getNewPassword()) || whiteSpace(change.getConfirmPassword())){
            throw new IllegalArgumentException("Password cannot be whitespace");
        }
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = change.getAccount().replaceAll("\\s+","");
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                Map<String, List<String>> attributes = user.getAttributes();
                if(attributes == null){
                    throw new NotFoundExceptionClass("User not found.");
                } else if (!attributes.containsKey("otpCode")) {
                    throw new IllegalArgumentException("Required to send otpCode");
                } else if(user.getAttributes().get("otpCode").get(0).equalsIgnoreCase(change.getOtpCode().replaceAll("\\s+",""))){

                    if(!change.getNewPassword().equalsIgnoreCase(change.getConfirmPassword())){
                        throw new IllegalArgumentException("Password not matched");
                    }
                    CredentialRepresentation passwordCredential = new CredentialRepresentation();
                    passwordCredential.setType(CredentialRepresentation.PASSWORD);
                    passwordCredential.setValue(change.getNewPassword());
                    passwordCredential.setTemporary(false);
                    resource(UUID.fromString(user.getId())).resetPassword(passwordCredential);
                    return returnUser(user);
                }else{
                    throw new IllegalArgumentException("Incorrect otpCode");
                }
            }
        }
        throw new NotFoundExceptionClass("User not found.");
    }

    @Override
    public RequestResetPassword sendOptCode(RequestResetPassword reset) throws MessagingException {
        User account = findEmail(reset.getAccount());
        if (account.getEmail().equalsIgnoreCase(reset.getAccount()) || account.getUsername().equalsIgnoreCase(reset.getAccount())) {
            setAttribute(findUser(reset.getAccount()),reset.getAccount().replaceAll("\\s+",""));
            emailService.resetPassword(reset.getAccount().replaceAll("\\s+",""));
            return reset;
        }
        throw new NotFoundExceptionClass("User not found.");
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public List<User> findByEmail(String email) {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().searchByEmail(email.replaceAll("\\s+",""),true);

        if(userRepresentations.stream().toList().isEmpty()){
            throw new NotFoundExceptionClass("User not found.");
        }

        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public User getUserById(UUID id) {
        // User Validation
        resource(id);
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
                .map(roleName -> Role.valueOf(roleName.toUpperCase()))
                .collect(Collectors.toList());
    }

    // Returning UserResource by id
    public UserResource resource(UUID id){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getId().equalsIgnoreCase(String.valueOf(id))){
                return keycloak.realm(realm).users().get(String.valueOf(id));
            }
        }
        throw new NotFoundExceptionClass("User not found");
    }

    // Validating Account
    public String accessTokenResponse(VerifyLogin login){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(login.getAccount().replaceAll("\\s+",""))){
               return myKeyCloak(user.getUsername(),login.getPassword());
            }}
        return myKeyCloak(login.getAccount().replaceAll("\\s+",""),login.getPassword());
    }

    // Returning Access Token
    public String myKeyCloak(String username, String password){
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .clientId(clientId)
                    .clientSecret(secretKey)
                    .username(username.toLowerCase().replaceAll("\\s+", ""))
                    .password(password)
                    .build();

            AccessTokenResponse tok = keycloak.tokenManager().getAccessToken();
            return tok.getToken();
        } catch (Exception e) {
            return null;
        }
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
    public void setAttribute (UserRepresentation user, String email) throws MessagingException {
        user.singleAttribute("otpCode",String.valueOf(emailService.verifyCode(email)));
        resource(UUID.fromString(user.getId())).update(user);
    }

    // Validating Existing Account
    public void existingAccount(String email, String username){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(email)){
                throw new IllegalArgumentException("This email is already exist");
            }else if(user.getUsername().equalsIgnoreCase(username)){
                throw new IllegalArgumentException("This username is already exist");
            }
        }
    }

    // Find Email
    public User findEmail(String account){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(account) || user.getUsername().equalsIgnoreCase(account)){
                return returnUser(user);
            }
        }
        throw new NotFoundExceptionClass("User not found");
    }

    // Return UserRepresentation
    public UserRepresentation findUser(String email){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(email) || user.getUsername().equalsIgnoreCase(email)){
                return user;
            }
        }
        throw new NotFoundExceptionClass("User not found");
    }

    // Validation Whitespace
    public boolean whiteSpace(String data){
        for (char c : data.toCharArray()) {
            if (Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

}
