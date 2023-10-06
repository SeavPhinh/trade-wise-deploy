package com.example.userservice.model;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserDto {

    public static User toDto(UserRepresentation userRepresentation) {

        User user = new User();

        // Converting String to List<Role>
        String listRole = userRepresentation.getAttributes().get("role").get(0);
        List<String> rolesList = Arrays.asList(listRole.replaceAll("\\[|\\]", "").split(", "));
        List<Role> roles = rolesList.stream()
                .map(Role::valueOf)
                .collect(Collectors.toList());

        user.setId(UUID.fromString(userRepresentation.getId()));
        user.setUsername(userRepresentation.getUsername());
        user.setEmail(userRepresentation.getEmail());
        user.setFirstName(userRepresentation.getFirstName());
        user.setLastName(userRepresentation.getLastName());
        user.setRoles(roles);
        user.setCreatedDate(LocalDateTime.parse(userRepresentation.getAttributes().get("createdDate").get(0)));
        user.setLastModified(LocalDateTime.parse(userRepresentation.getAttributes().get("lastModified").get(0)));
        return user;
    }

}