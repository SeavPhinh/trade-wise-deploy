package com.example.userservice.service;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.userservice.model.UserDto;
import com.example.userservice.request.UserRequest;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public UserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }


    public List<User> getAllUsers() {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public List<User> findByUsername(String username) {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().search(username);
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

    public void deleteUser(UUID userId){
        keycloak.realm(realm).users().delete(String.valueOf(userId));
    }

    public User updateUser(UUID id, UserRequest request) {

        UserResource existingUserResource = keycloak.realm(realm).users().get(String.valueOf(id));
        UserRepresentation updatedUser = new UserRepresentation();

        updatedUser.setUsername(request.getUsername());
        updatedUser.setFirstName(request.getFirstname());
        updatedUser.setLastName(request.getLastname());
        updatedUser.setEmail(request.getEmail());
        updatedUser.singleAttribute("createdDate", String.valueOf(existingUserResource.toRepresentation().getAttributes().get("createdDate").get(0)));
        updatedUser.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
        existingUserResource.update(updatedUser);
        UserRepresentation updatedUserRepresentation = existingUserResource.toRepresentation();


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

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    public List<User> findByEmail(String email) {  List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().search(email);
        return userRepresentations.stream()
                .map(UserDto::toDto)
                .collect(Collectors.toList());
    }

    public User getUserById(UUID id) {
        UserResource represent = keycloak.realm(realm).users().get(String.valueOf(id));
        User responseUser = new User();

        responseUser.setId(UUID.fromString(represent.toRepresentation().getId()));
        responseUser.setUsername(represent.toRepresentation().getUsername());
        responseUser.setEmail(represent.toRepresentation().getUsername());
        responseUser.setFirstName(represent.toRepresentation().getFirstName());
        responseUser.setLastName(represent.toRepresentation().getLastName());
        responseUser.setCreatedDate(LocalDateTime.now());
        responseUser.setLastModified(LocalDateTime.now());

        return responseUser;
    }

    public List<Role> roles(String role){
        List<String> rolesList = Arrays.asList(role.replaceAll("\\[|\\]", "").split(", "));
        return rolesList.stream()
                .map(Role::valueOf)
                .collect(Collectors.toList());
    }

}
