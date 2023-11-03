package com.example.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;

@EnableWebSecurity
@Configuration
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class KeyCloakSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors()
                .and()
                .authorizeHttpRequests(authorize -> {
                    //For OpenAPI
                    authorize.requestMatchers("user-service/v3/api-docs/**", "user-service/swagger-ui/**", "user-service/swagger-ui.html").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/users").permitAll();
                    authorize.requestMatchers(HttpMethod.POST,"/api/v1/users").permitAll();
                    authorize.requestMatchers("/api/v1/third-party/modify").permitAll();
                    authorize.requestMatchers("/api/v1/users/login").permitAll();
                    authorize.requestMatchers("/api/v1/users/username").permitAll();
                    authorize.requestMatchers("/api/v1/users/verify").permitAll();
                    authorize.requestMatchers("/api/v1/users/reset-password").permitAll();
                    authorize.requestMatchers("/api/v1/users/users/email").permitAll();
                    authorize.requestMatchers(HttpMethod.POST,"/api/v1/users/otp/reset-password").permitAll();
                    authorize.requestMatchers(HttpMethod.GET,"/api/v1/users/{id}").permitAll();
                    authorize.anyRequest().authenticated();
                }).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*")); // Allows all origin
        config.setAllowedHeaders(
                Arrays.asList(
                        "X-Requested-With",
                        "Origin",
                        "Content-Type",
                        "Accept",
                        "Authorization",
                        "Access-Control-Allow-Credentials",
                        "Access-Control-Allow-Headers",
                        "Access-Control-Allow-Methods",
                        "Access-Control-Allow-Origin",
                        "Access-Control-Expose-Headers",
                        "Access-Control-Max-Age",
                        "Access-Control-Request-Headers",
                        "Access-Control-Request-Method",
                        "Age",
                        "Allow",
                        "Alternates",
                        "Content-Range",
                        "Content-Disposition",
                        "Content-Description"
                )
        );
        config.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH")
        );
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
