package com.example.userservice.service.group;
import com.example.userservice.model.group.Group;
import com.example.userservice.model.user.User;
import com.example.userservice.request.group.GroupRequest;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GroupService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public GroupService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public List<Group> getAllGroups() {
        List<GroupRepresentation> groupRepresentations = keycloak.realm(realm).groups().groups();
        List<Group> groups = new ArrayList<>();

        for (GroupRepresentation groupRepresentation : groupRepresentations) {
            Group group = new Group();
            group.setId(UUID.fromString(groupRepresentation.getId()));
            group.setName(groupRepresentation.getName());
            groups.add(group);
        }
        return groups;
    }

    public Group createGroup(GroupRequest request) {

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(request.getName());
        Response response = keycloak.realm(realm).groups().add(groupRepresentation);

        Group group = new Group();
        group.setId(UUID.fromString(response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1")));
        group.setName(request.getName());
        return  group;
    }

    public void deleteGroup(UUID id) {
        keycloak.realm(realm).groups().group(String.valueOf(id)).remove();
    }

    public Group updateGroup(UUID id, GroupRequest request) {

        GroupRepresentation groupRepresentation = new GroupRepresentation();
        groupRepresentation.setName(request.getName());
        keycloak.realm(realm).groups().group(String.valueOf(id)).update(groupRepresentation);
        Group group = new Group();
        group.setId(id);
        group.setName(request.getName());
        return  group;
    }

    public Group getById(UUID id) {
        GroupRepresentation group = keycloak.realm(realm).groups().group(id.toString()).toRepresentation();
        return new Group(UUID.fromString(group.getId()),group.getName());
    }

    public void addUserToGroup(UUID groupId, UUID userId) {
        keycloak.realm(realm).users().get(userId.toString()).joinGroup(groupId.toString());
    }

    public List<User> getMemberGroup(UUID groupId) {
        List<UserRepresentation> userPre = keycloak.realm(realm).groups().group(groupId.toString()).members();

        List<User> member = new ArrayList<>();

        for (UserRepresentation userRepresentation : userPre) {
            if (userRepresentation != null) {
                User user = new User(
                        UUID.fromString(userRepresentation.getId()),
                        userRepresentation.getUsername(),
                        userRepresentation.getEmail(),
                        userRepresentation.getFirstName(),
                        userRepresentation.getLastName(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                member.add(user);
            }
        }
        return member;
    }

}
