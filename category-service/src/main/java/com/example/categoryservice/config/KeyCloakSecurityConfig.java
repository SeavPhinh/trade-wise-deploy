package com.example.categoryservice.config;

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
                    authorize.requestMatchers("category-service/v3/api-docs/**", "category-service/swagger-ui/**", "category-service/swagger-ui.html").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/categories").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/categories/{id}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/category/subcategories/{id}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/categories/{name}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/subcategories/{id}").permitAll();
                    authorize.anyRequest().authenticated();
                }).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
