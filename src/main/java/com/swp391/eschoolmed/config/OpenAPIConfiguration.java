package com.swp391.eschoolmed.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer", description = "A JWT token is required to access this API. JWT token can be obtain by api/accounts/login "
        +
        "} API")
public class OpenAPIConfiguration {
    @Value("${springdoc.servers}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info().title("REST API").version("v3"))
                .addServersItem(new Server().url(serverUrl).description("API Gateway"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }

}