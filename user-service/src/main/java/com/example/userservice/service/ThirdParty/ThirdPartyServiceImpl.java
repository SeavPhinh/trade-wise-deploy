package com.example.userservice.service.ThirdParty;

import com.example.commonservice.enumeration.Role;
import com.example.commonservice.model.User;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public ThirdPartyServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public User modifyGmailAccount() {

        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list();
        for (UserRepresentation user: userRepresentations) {
            if(!keycloak.realm(realm).users().get(user.getId()).toRepresentation().getFederatedIdentities().isEmpty()){

                user.singleAttribute("role", String.valueOf(List.of(Role.BUYER,Role.SELLER)));
                user.singleAttribute("createdDate", String.valueOf(LocalDateTime.now()));
                user.singleAttribute("lastModified", String.valueOf(LocalDateTime.now()));
                keycloak.realm(realm).users().get(user.getId()).update(user);

                System.out.println("IDP: " + keycloak.realm(realm).users().get(user.getId()).toRepresentation().getFederatedIdentities().get(0).getIdentityProvider());
            }else{
                System.out.println("Not Provider IDP");
            }
        }

        return null;
    }

}
