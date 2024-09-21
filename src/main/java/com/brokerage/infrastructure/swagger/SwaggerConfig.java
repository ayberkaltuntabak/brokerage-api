package com.brokerage.infrastructure.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info().title("Brokerage Firm API")
                        .version("1.0")
                        .description("API documentation for Brokerage Firm backend"))
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                            new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .in(SecurityScheme.In.HEADER)
                                                .name("Authorization")));
  }
}
