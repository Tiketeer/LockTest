package com.tiketeer.Tiketeer.configuration;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SecurityScheme(
	name = "Authorization",
	type = SecuritySchemeType.HTTP,
	bearerFormat = "JWT",
	scheme = "bearer"
)
@OpenAPIDefinition(
	info = @Info(title = "Tiketeer API Swagger", description = "API 명세를 위한 Swagger 페이지")
)
@Configuration
public class SwaggerConfig {
}
