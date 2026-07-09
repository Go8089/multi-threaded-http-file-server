package com.goMaddy.multithreaded_http_fileserver.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
@Bean
public OpenAPI fileServerOpenAPI() {
    return new OpenAPI().info(new Info()
                            .title("Multi-Threaded HTTP File Server API")
                            .description(""" 
                                Secure REST API for uploading, downloading and managing files.
                                    Features:
                                    • JWT Authentication
                                    • File Upload
                                    • File Download
                                    • Search
                                    • Pagination
                                    • Validation
                                    • Logging
                                    """)
                            .version("1.0.0")
                            .contact(
                                    new Contact()
                                            .name("Gopal Jha")
                                            .email("go@gmail.com")
                            )
            )

            .externalDocs(
                    new ExternalDocumentation()
                            .description("GitHub Repository")
                            .url("https://github.com/Go8089/multi-threaded-http-file-server")
            )

            .addSecurityItem(
                    new SecurityRequirement()
                            .addList("Bearer Authentication")
            )

            .components(
                    new Components()
                            .addSecuritySchemes(
                                    "Bearer Authentication",

                                    new SecurityScheme()
                                            .name("Bearer Authentication")
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                            )
            );
}
   }