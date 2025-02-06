package com.gymcrm.configuration.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
	@Value("${gym.crm.cors.origins}")
	private List<String> origins;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins(origins.toArray(new String[0])).allowedMethods(
				        HttpMethod.GET.toString(), HttpMethod.DELETE.toString(), HttpMethod.PUT.toString(),
				        HttpMethod.PATCH.toString(), HttpMethod.POST.toString());
			}
		};
	}
}
