package com.example.userservice.service.ThirdParty;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.exception.NotFoundExceptionClass;
import com.example.commonservice.model.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public ThirdPartyServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public List<User> modifyGmailAccount() {

        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        List<User> responses = new ArrayList<>();
        for (UserRepresentation user: userRepresentations) {
            if(!keycloak.realm(realm).users().get(user.getId()).toRepresentation().getFederatedIdentities().isEmpty()){
                Map<String, List<String>> attributes = user.getAttributes();
                if(attributes == null){
                    user.singleAttribute("role", String.valueOf(List.of(Role.BUYER,Role.SELLER)));
                    user.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
                    user.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
                    user.setEnabled(true);
                    keycloak.realm(realm).users().get(user.getId()).update(user);
                    responses.add(new User(
                            UUID.fromString(user.getId()),
                            user.getUsername(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            roles(user.getAttributes().get("role").get(0)),
                            LocalDateTime.parse(user.getAttributes().get("createdDate").get(0)),
                            LocalDateTime.parse(user.getAttributes().get("lastModified").get(0))
                    ));
                }
            }
        }

        return Optional.of(responses)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new NotFoundExceptionClass("Nothing to modify"));

    }
    public List<Role> roles(String role){
        List<String> rolesList = Arrays.asList(role.replaceAll("\\[|\\]", "").split(", "));
        return rolesList.stream()
                .map(Role::valueOf)
                .collect(Collectors.toList());
    }
}
