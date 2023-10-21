package com.example.productforsaleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ProductForSaleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductForSaleServiceApplication.class, args);
    }

}
