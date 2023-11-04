package com.example.chatservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        servers = @Server(url = "/", description = "Default Server URL"),
        info = @Info(
                title = "Chat Service",
                description = "Started From 04/10/2023",
                version = "1.0.0"
        )
)
@SecurityScheme(
        name = "oAuth2",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        flows = @OAuthFlows(
                clientCredentials = @OAuthFlow(
                        tokenUrl = "https://keycloak.trade-wise.online/auth/realms/go-selling-api/protocol/openid-connect/token"
                )
                ,
                password = @OAuthFlow(
                        tokenUrl = "https://keycloak.trade-wise.online/auth/realms/go-selling-api/protocol/openid-connect/token"
                )
        )
)
public class OpenApiConfig { }
