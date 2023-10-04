package com.example.userservice.model.user;

import org.keycloak.representations.idm.UserRepresentation;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDto {

    public static User toDto(UserRepresentation userRepresentation) {

        User user = new User();
        user.setId(UUID.fromString(userRepresentation.getId()));
        user.setUsername(userRepresentation.getUsername());
        user.setEmail(userRepresentation.getEmail());
        user.setFirstName(userRepresentation.getFirstName());
        user.setLastName(userRepresentation.getLastName());
        user.setCreatedDate(LocalDateTime.parse(userRepresentation.getAttributes().get("createdDate").get(0)));
        user.setLastModified(LocalDateTime.parse(userRepresentation.getAttributes().get("lastModified").get(0)));
        return user;
    }
}
