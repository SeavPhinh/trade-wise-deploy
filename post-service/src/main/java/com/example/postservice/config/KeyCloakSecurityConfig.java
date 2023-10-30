package com.example.postservice.config;

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
                    authorize.requestMatchers("post-service/v3/api-docs/**", "post-service/swagger-ui/**", "post-service/swagger-ui.html").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts/{id}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts/alphabet").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts/sub-category/list").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts/oldest").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts/newest").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts/image/{fileName}").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"api/v1/posts/budget").permitAll();
                    authorize.anyRequest().authenticated();
                }).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
