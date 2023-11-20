package com.example.userservice.service.User;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.userservice.exception.NotFoundExceptionClass;
import com.example.userservice.model.*;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
            throw new NotFoundExceptionClass(ValidationConfig.EMPTY_USER);
        }
        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public List<User> findByUsername(String username) {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().search(username.replaceAll("\\s+",""));
        if(userRepresentations.stream().toList().isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
        }
        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public UserCreated postUser(UserRequest request) throws MessagingException {

        List<String> roles = new ArrayList<>();
        roles.add(String.valueOf(Role.SELLER));
        roles.add(String.valueOf(Role.BUYER));

        UsersResource usersResource = keycloak.realm(realm).users();
        if(whiteSpace(request.getPassword())){
            throw new IllegalArgumentException(ValidationConfig.WHITE_SPACE);
        }

        isValidString(request.getFirstname());
        isValidString(request.getLastname());

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(request.getPassword());

        // Validation Existing Account
        existingAccount(request.getEmail(),request.getUsername().replaceAll("\\s+",""));

        UserRepresentation userPre = new UserRepresentation();
        userPre.setUsername(request.getUsername().toLowerCase().replaceAll("\\s+",""));
        userPre.setCredentials(Collections.singletonList(credentialRepresentation));
        userPre.setFirstName(request.getFirstname());
        userPre.setLastName(request.getLastname());
        userPre.setEmail(request.getEmail().toLowerCase());

        userPre.singleAttribute("role", String.valueOf(roles(String.valueOf(roles))));
        userPre.singleAttribute("created_date", String.valueOf(LocalDateTime.now()));
        userPre.singleAttribute("last_modified", String.valueOf(LocalDateTime.now()));
        userPre.singleAttribute("logged_as", String.valueOf(Role.BUYER));
        userPre.singleAttribute("otp_code",String.valueOf(emailService.verifyCode(request.getEmail())));
        userPre.singleAttribute("is_verify",String.valueOf(false));
        userPre.setEnabled(true);
        usersResource.create(userPre);

        UserRepresentation createdUserRepresentation =
                keycloak.realm(realm).users().search(request.getUsername().replaceAll("\\s+","").toLowerCase()).get(0);

        return new UserCreated(
                UUID.fromString(createdUserRepresentation.getId()),
                createdUserRepresentation.getUsername().toLowerCase().replaceAll("\\s+",""),
                createdUserRepresentation.getEmail().toLowerCase(),
                createdUserRepresentation.getFirstName(),
                createdUserRepresentation.getLastName(),
                Boolean.valueOf(userPre.getAttributes().get("is_verify").get(0)),
                roles(userPre.getAttributes().get("role").get(0)),
                LocalDateTime.parse(userPre.getAttributes().get("created_date").get(0)),
                LocalDateTime.parse(userPre.getAttributes().get("last_modified").get(0))
        );
    }

    public Void deleteUser(UUID userId) {
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if (user.getId().equalsIgnoreCase(String.valueOf(getUserById(userId).getId()))) {
                keycloak.realm(realm).users().delete(String.valueOf(userId));
                return null;
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    @Override
    public UserResponse verifiedAccount(Role role, VerifyLogin login){

        roleCheck(role);
        String token;

        if(accessTokenResponse(login.getAccount(),login.getPassword()) == null){
            throw new IllegalArgumentException(ValidationConfig.USER_INVALID);
        }
        token = accessTokenResponse(login.getAccount(),login.getPassword());

        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = login.getAccount().replaceAll("\\s+","");
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                user.singleAttribute("logged_as", role.name());
                Map<String, List<String>> attributes = user.getAttributes();
                if(attributes == null){
                    throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
                } else if (!attributes.containsKey("otp_code")) {
                    throw new IllegalArgumentException(ValidationConfig.REQUIRED_OTP);
                } else if(user.getAttributes().get("otp_code").get(0).equalsIgnoreCase(login.getOtpCode().replaceAll("\\s+",""))){
                    user.singleAttribute("is_verify", String.valueOf(true));
                    resource(UUID.fromString(user.getId())).update(user);
                     return responseUser(user,token);
                }else{
                    throw new IllegalArgumentException(ValidationConfig.INVALID_OTP);
                }
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    @Override
    public User resetPassword(ResetPassword change){

        if(whiteSpace(change.getNewPassword()) || whiteSpace(change.getConfirmPassword())){
            throw new IllegalArgumentException(ValidationConfig.WHITE_SPACE);
        }

        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            String accountId = change.getAccount().replaceAll("\\s+","");
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {
                Map<String, List<String>> attributes = user.getAttributes();
                if(attributes == null){
                    throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
                } else if (!attributes.containsKey("otp_code")) {
                    throw new IllegalArgumentException(ValidationConfig.REQUIRED_OTP);
                } else if(user.getAttributes().get("otp_code").get(0).equalsIgnoreCase(change.getOtpCode().replaceAll("\\s+",""))){
                    if(!change.getNewPassword().equalsIgnoreCase(change.getConfirmPassword())){
                        throw new IllegalArgumentException(ValidationConfig.NOT_MATCHES_PASSWORD);
                    }
                    CredentialRepresentation passwordCredential = new CredentialRepresentation();
                    passwordCredential.setType(CredentialRepresentation.PASSWORD);
                    passwordCredential.setValue(change.getNewPassword());
                    passwordCredential.setTemporary(false);
                    resource(UUID.fromString(user.getId())).resetPassword(passwordCredential);
                    return returnUser(user);
                }else{
                    throw new IllegalArgumentException(ValidationConfig.INVALID_OTP);
                }
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    @Override
    public RequestResetPassword sendOptCode(RequestResetPassword reset) throws MessagingException {
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(reset.getEmail())){
                setAttribute(findUser(reset.getEmail()),reset.getEmail().replaceAll("\\s+",""));
//                emailService.resetPassword(reset.getEmail().replaceAll("\\s+",""));
                return reset;
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    @Override
    public UserResponse loginAccount(Role role, UserLogin login) {

        roleCheck(role);
        String token;

        if(accessTokenResponse(login.getAccount(),login.getPassword()) == null){
            throw new IllegalArgumentException(ValidationConfig.USER_INVALID);
        }
        token = accessTokenResponse(login.getAccount(),login.getPassword());

        for (UserRepresentation user : keycloak.realm(realm).users().list()) {

            String accountId = login.getAccount().replaceAll("\\s+","");
            if (user.getEmail().equalsIgnoreCase(accountId) || user.getUsername().equalsIgnoreCase(accountId)) {

                user.singleAttribute("logged_as", role.name());
                Map<String, List<String>> attributes = user.getAttributes();
                if(attributes == null){
                    throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
                } else if (user.getAttributes().get("is_verify").get(0).equalsIgnoreCase("true")) {
                    resource(UUID.fromString(user.getId())).update(user);
                    return responseUser(user,token);
                }else{
                    throw new IllegalArgumentException(ValidationConfig.ILLEGAL_USER);
                }
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    @Override
    public User getCurrentUser() {
        return getUserById(UUID.fromString(currentUser()));
    }

    public UserResponse responseUser(UserRepresentation user, String token){
        return new UserResponse(
                UUID.fromString(resource(UUID.fromString(user.getId())).toRepresentation().getId()),
                resource(UUID.fromString(user.getId())).toRepresentation().getUsername(),
                resource(UUID.fromString(user.getId())).toRepresentation().getEmail(),
                resource(UUID.fromString(user.getId())).toRepresentation().getFirstName(),
                resource(UUID.fromString(user.getId())).toRepresentation().getLastName(),
                roles(resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("role").get(0)),
                resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("logged_as").get(0),
                token,
                LocalDateTime.parse(user.getAttributes().get("created_date").get(0)),
                LocalDateTime.parse(user.getAttributes().get("last_modified").get(0))
        );
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    // Account not yet verify
    public void isNotVerify(UUID id){
        UserRepresentation user = keycloak.realm(realm).users().get(String.valueOf(id)).toRepresentation();
        if(!user.getAttributes().get("is_verify").get(0).equalsIgnoreCase("true")){
            throw new IllegalArgumentException(ValidationConfig.ILLEGAL_USER);
        }
    }

    public List<User> findByEmail(String email) {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().searchByEmail(email.replaceAll("\\s+",""),true);

        if(userRepresentations.stream().toList().isEmpty()){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
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
                Boolean.valueOf(resource(id).toRepresentation().getAttributes().get("is_verify").get(0)),
                roles(resource(id).toRepresentation().getAttributes().get("role").get(0)),
                resource(id).toRepresentation().getAttributes().get("logged_as").get(0),
                LocalDateTime.parse(resource(id).toRepresentation().getAttributes().get("created_date").get(0)),
                LocalDateTime.parse(resource(id).toRepresentation().getAttributes().get("last_modified").get(0))
        );
    }

    // Converting Role from Attribute as String to ArrayList
    public List<Role> roles(String role){
        List<String> rolesList = Arrays.asList(role.replaceAll(ValidationConfig.REGEX_ROLES, "").split(", "));
        return rolesList.stream()
                .map(roleName -> Role.valueOf(roleName.toUpperCase()))
                .collect(Collectors.toList());
    }

    // Validation Role
    public void roleCheck(Role role){
        if(role == null){
            throw new IllegalArgumentException(ValidationConfig.ROLE_REQUIRED_MESSAGE);
        }
    }

    // Returning UserResource by id
    public UserResource resource(UUID id){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getId().equalsIgnoreCase(String.valueOf(id))){
                return keycloak.realm(realm).users().get(String.valueOf(id));
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    // Validating Account
    public String accessTokenResponse(String account, String password){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(account.replaceAll("\\s+",""))){
               return myKeyCloak(user.getUsername(),password);
            }}
        return myKeyCloak(account.replaceAll("\\s+",""),password);
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
                Boolean.valueOf(resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("is_verify").get(0)),
                roles(resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("role").get(0)),
                resource(UUID.fromString(user.getId())).toRepresentation().getAttributes().get("logged_as").get(0),
                LocalDateTime.parse(user.getAttributes().get("created_date").get(0)),
                LocalDateTime.parse(user.getAttributes().get("last_modified").get(0))
        );
    }

    // Set otp attribute for user
    public void setAttribute (UserRepresentation user, String email) throws MessagingException {
        user.singleAttribute("otp_code",String.valueOf(emailService.resetPassword(email)));
        resource(UUID.fromString(user.getId())).update(user);
    }

    // Validating Existing Account
    public void existingAccount(String email, String username){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(email)){
                throw new IllegalArgumentException(ValidationConfig.EXISTING_EMAIL);
            }else if(user.getUsername().equalsIgnoreCase(username)){
                throw new IllegalArgumentException(ValidationConfig.EXISTING_USERNAME);
            }
        }
    }

    // Return UserRepresentation
    public UserRepresentation findUser(String email){
        for (UserRepresentation user : keycloak.realm(realm).users().list()) {
            if(user.getEmail().equalsIgnoreCase(email) || user.getUsername().equalsIgnoreCase(email)){
                return user;
            }
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
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

    // Returning Token
    public String currentUser(){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
                // Decode to Get User Id
            DecodedJWT decodedJWT = JWT.decode(jwt.getTokenValue());
                return decodedJWT.getSubject();
            }
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
        }
        throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
    }

    // firstname and lastname Validating
    public void isValidString(String data){
        try {
            if(data.matches(".*\\d+.*")){
                throw new IllegalArgumentException(ValidationConfig.INVALID_STRING);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(ValidationConfig.INVALID_STRING);
        }
    }

}
