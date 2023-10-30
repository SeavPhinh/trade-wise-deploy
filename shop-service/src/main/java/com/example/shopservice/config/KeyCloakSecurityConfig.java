package com.example.shopservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class KeyCloakSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> {
                    //For OpenAPI
                    authorize.requestMatchers("shop-service/v3/api-docs/**", "shop-service/swagger-ui/**", "shop-service/swagger-ui.html").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/shops").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/shops/filter").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/shops/sort").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/shops/best").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/shops/{id}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/shops/user/{userId}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/shops/image").permitAll();
                    authorize.anyRequest().authenticated();
                }).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
