package com.gymcrm.configuration;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
		        .securitySchemes(Collections.singletonList(apiKey()))
		        .securityContexts(Collections.singletonList(securityContext())).select()
		        .apis(RequestHandlerSelectors.basePackage("com.gymcrm")).paths(PathSelectors.any()).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Gym CRM API").description("API Documentation for Gym CRM Application")
		        .version("1.0").build();
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(Collections.singletonList(new SecurityReference("JWT",
		        new AuthorizationScope[]{new AuthorizationScope("global", "accessEverything")}))).build();
	}

	private ApiKey apiKey() {
		return new ApiKey("JWT", "Authorization", "header");
	}
}
