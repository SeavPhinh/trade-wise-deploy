package com.example.manageuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserInfoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserInfoServiceApplication.class, args);
    }

}
