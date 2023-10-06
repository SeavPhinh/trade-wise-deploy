package com.example.userservice.service.ThirdParty;
import com.example.commonservice.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ThirdPartyService {

    List<User> modifyGmailAccount();
}
