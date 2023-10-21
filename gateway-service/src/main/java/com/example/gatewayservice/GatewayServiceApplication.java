package com.example.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(r -> r.path("/category-service/v3/api-docs").uri("lb://category-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://category-service"))

                .route(r -> r.path("/chat-service/v3/api-docs").uri("lb://chat-service"))
                .route(r -> r.path("**").uri("lb://chat-service"))

                .route(r -> r.path("/post-service/v3/api-docs").uri("lb://post-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://post-service"))

                .route(r -> r.path("/product-for-sale-service/v3/api-docs").uri("lb://product-for-sale-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://product-for-sale-service"))

                .route(r -> r.path("/product-service/v3/api-docs").uri("lb://product-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://product-service"))

                .route(r -> r.path("/shop-service/v3/api-docs").uri("lb://shop-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://shop-service"))

                .route(r -> r.path("/user-info-service/v3/api-docs").uri("lb://user-info-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://user-info-service"))

                .route(r -> r.path("/user-service/v3/api-docs").uri("lb://user-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://user-service"))

                .route(r -> r.path("/common-service/v3/api-docs").uri("lb://common-service"))
                .route(r -> r.path("/api/v1/**").uri("lb://common-service"))
                .build();
    }

}
