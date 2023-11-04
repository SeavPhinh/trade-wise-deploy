package com.example.chatservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class MessageModel {

    @Id
    @GeneratedValue
    private UUID id;
    @Column(columnDefinition = "JSON")
    private String content;
    private UUID senderId;
    private UUID receiverId;
    private LocalDateTime timestamp;
    private Boolean status;

}

