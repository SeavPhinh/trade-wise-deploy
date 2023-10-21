package com.example.userservice.config;

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
                    authorize.requestMatchers("user-service/v3/api-docs/**", "user-service/swagger-ui/**", "user-service/swagger-ui.html").permitAll();
                    authorize.requestMatchers("/api/v1/users").permitAll();
                    authorize.requestMatchers("/api/v1/third-party/modify").permitAll();
                    authorize.requestMatchers("/api/v1/users/login").permitAll();
                    authorize.requestMatchers("/api/v1/users/username").permitAll();
                    authorize.requestMatchers("/api/v1/users/verify").permitAll();
                    authorize.requestMatchers("/api/v1/users/reset-password").permitAll();
                    authorize.requestMatchers("/api/v1/users/users/email").permitAll();
                    authorize.requestMatchers("/api/v1/users/otp-reset-password").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/users/{id}").permitAll();
                    authorize.anyRequest().authenticated();
                }).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
