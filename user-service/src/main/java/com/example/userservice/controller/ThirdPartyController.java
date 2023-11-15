package com.example.userservice.controller;

import com.example.commonservice.model.User;
import com.example.commonservice.response.ApiResponse;
import com.example.userservice.service.ThirdParty.ThirdPartyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/third-party")
@Tag(name = "Third Party")
@CrossOrigin
public class ThirdPartyController {
    private final ThirdPartyService thirdPartyService;
    public ThirdPartyController(ThirdPartyService thirdPartyService) {
        this.thirdPartyService = thirdPartyService;
    }

    @PutMapping("/modify")
    @Operation(summary = "modified to set attribute account")
    public ResponseEntity<ApiResponse<List<User>>> modifyingAccount(){
        return new ResponseEntity<>(new ApiResponse<>(
                "Account modified successfully",
                thirdPartyService.modifyGmailAccount(),
                HttpStatus.OK
        ), HttpStatus.OK);
    }
}
