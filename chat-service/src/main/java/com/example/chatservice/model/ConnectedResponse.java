package com.example.chatservice.model;

import com.example.commonservice.response.UserContact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectedResponse {

    private UserContact user;
    private MessageResponse message;
    private Integer unseenCount;

}
