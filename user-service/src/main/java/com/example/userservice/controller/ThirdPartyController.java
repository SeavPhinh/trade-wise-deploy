package com.example.userservice.controller;

import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.userservice.service.ThirdParty.ThirdPartyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1")
public class ThirdPartyController {

    private final ThirdPartyService thirdPartyService;

    public ThirdPartyController(ThirdPartyService thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }

    @PutMapping("/gmail")
    public ResponseEntity<ApiResponse<User>> modifyGmailAccount(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Gmail account modified successfully",
                thirdPartyService.modifyGmailAccount(),
                HttpStatus.ACCEPTED
        ), HttpStatus.ACCEPTED);
    }
}
