package com.example.userservice.model;

import com.example.commonservice.config.ValidationConfig;
import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.userservice.exception.NotFoundExceptionClass;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserDto {

    public static User toDto(UserRepresentation userRepresentation) {
        try{
            User user = new User();
            // Converting String to List<Role>
            String listRole = userRepresentation.getAttributes().get("role").get(0);
            List<String> rolesList = Arrays.asList(listRole.replaceAll(ValidationConfig.REGEX_ROLES, "").split(", "));
            List<Role> roles = rolesList.stream()
                    .map(Role::valueOf)
                    .collect(Collectors.toList());

            user.setId(UUID.fromString(userRepresentation.getId()));
            user.setUsername(userRepresentation.getUsername());
            user.setEmail(userRepresentation.getEmail());
            user.setFirstName(userRepresentation.getFirstName());
            user.setLastName(userRepresentation.getLastName());
            user.setIsVerify(Boolean.valueOf(userRepresentation.getAttributes().get("is_verify").get(0)));
            user.setRoles(roles);
            user.setLoggedAs(userRepresentation.getAttributes().get("logged_as").get(0));
            user.setCreatedDate(LocalDateTime.parse(userRepresentation.getAttributes().get("created_date").get(0)));
            user.setLastModified(LocalDateTime.parse(userRepresentation.getAttributes().get("last_modified").get(0)));
            return user;
        }catch (Exception e){
            throw new NotFoundExceptionClass(ValidationConfig.NOTFOUND_USER);
        }
    }

}
