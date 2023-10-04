package com.example.userservice.controller;
import com.example.userservice.model.group.Group;
import com.example.userservice.model.user.User;
import com.example.userservice.request.group.GroupRequest;
import com.example.userservice.service.group.GroupService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@SecurityRequirement(name = "oAuth2")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/groups")
    public List<Group> getAllGroup(){
        return groupService.getAllGroups();
    }

    @GetMapping("/groups/{id}")
    public Group getAllGroup(@PathVariable UUID id){
        return groupService.getById(id);
    }

    @PostMapping("/groups")
    public Group createGroup(@RequestBody GroupRequest request){
        return groupService.createGroup(request);
    }

    @DeleteMapping("/groups/{id}")
    public String deleteGroup(@PathVariable UUID id){
        groupService.deleteGroup(id);
        return "Delete Successfully.";
    }

    @PutMapping("/groups/{id}")
    public Group updateGroup(@PathVariable UUID id,
                             @RequestBody GroupRequest request){
        return groupService.updateGroup(id,request);
    }

    @PostMapping("/groups/{groupId}/users/{userId}")
    public String addUserToGroup(@PathVariable UUID groupId,
                                 @PathVariable UUID userId){
        groupService.addUserToGroup(groupId,userId);
        return "Add Successfully.";
    }

    @GetMapping("/groups/{groupId}/users")
    public List<User> getUserInGroup(@PathVariable UUID groupId){
        return groupService.getMemberGroup(groupId);
    }

}
