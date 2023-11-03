package com.nvt.iot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    final String SECURITY_SCHEME_NAME = "Bearer Authentication";
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, createAPIKeyScheme())
            )
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("WATER LEVEL WEB-APP APIs")
            .description("API Endpoint Decoration")
            .version("1.0")
            .contact(new Contact()
                .name("NVT")
                .email( "vtpro2001@gmail.com").url("https://github.com/tamnguyen29")
            )
            .license(new License()
                .name("License of API")
                .url("API license URL")
            );
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .name(SECURITY_SCHEME_NAME)
            .bearerFormat("JWT")
            .scheme("bearer");
    }
}
