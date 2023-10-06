package com.example.userservice.controller;

import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.userservice.service.ThirdParty.ThirdPartyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class ThirdPartyController {

    private final ThirdPartyService thirdPartyService;

    public ThirdPartyController(ThirdPartyService thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }

    @PutMapping("/modify")
    public ResponseEntity<ApiResponse<List<User>>> modifyGmailAccount(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Account modified successfully",
                thirdPartyService.modifyGmailAccount(),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }
}