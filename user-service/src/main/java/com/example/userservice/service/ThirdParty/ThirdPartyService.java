package com.example.userservice.service.ThirdParty;

import com.example.commonservice.model.User;
import org.springframework.stereotype.Service;

@Service
public interface ThirdPartyService {

    User modifyGmailAccount();
}
