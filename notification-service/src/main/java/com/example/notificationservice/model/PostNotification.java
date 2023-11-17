package com.example.notificationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "post_notifications")
public class PostNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

}
