package com.example.notificationservice.config;

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
                    authorize.requestMatchers("notification-service/v3/api-docs/**", "notification-service/swagger-ui/**", "notification-service/swagger-ui.html").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/post-notification/{id}/{userId}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/post-notification/{userId}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/post-notification/unread/{userId}").permitAll();
                    authorize.anyRequest().authenticated();
                }).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
