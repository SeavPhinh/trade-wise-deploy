package com.example.userservice.service.ThirdParty;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import com.example.userservice.exception.NotFoundExceptionClass;
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
                    user.singleAttribute("is_verify", String.valueOf(true));
                    user.singleAttribute("created_date", String.valueOf(LocalDateTime.now()));
                    user.singleAttribute("last_modified", String.valueOf(LocalDateTime.now()));
                    user.setEnabled(true);

                    keycloak.realm(realm).users().get(user.getId()).update(user);
                    responses.add(new User(
                            UUID.fromString(user.getId()),
                            user.getUsername(),
                            user.getEmail(),
                            user.getFirstName(),
                            user.getLastName(),
                            Boolean.valueOf(user.getAttributes().get("is_verify").get(0)),
                            roles(user.getAttributes().get("role").get(0)),
                            user.getAttributes().get("logged_as").get(0),
                            LocalDateTime.parse(user.getAttributes().get("created_date").get(0)),
                            LocalDateTime.parse(user.getAttributes().get("last_modified").get(0))
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
