package com.gymcrm.configuration.security;

import com.gymcrm.configuration.security.auth.CustomAccessDeniedHandler;
import com.gymcrm.configuration.security.auth.CustomAuthenticationEntryPoint;
import com.gymcrm.configuration.security.auth.JwtAuthenticationFilter;
import com.gymcrm.configuration.security.auth.JwtAuthorizationFilter;
import com.gymcrm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
	private final JwtAuthorizationFilter jwtAuthorizationFilter;
	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,
	        CustomAccessDeniedHandler accessDeniedHandler, CustomAuthenticationEntryPoint authenticationEntryPoint)
	        throws Exception {
		JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil);
		http.cors().and().csrf().disable().authorizeHttpRequests(auth -> auth
		        .requestMatchers(antPathRequestMatcher("/actuator/health"),
		                antPathRequestMatcher("/users/me/login", HttpMethod.POST.name()),
		                antPathRequestMatcher("/users/me/logout"))
		        .permitAll()
		        .requestMatchers(antPathRequestMatcher("/swagger-ui/**"), antPathRequestMatcher("/v2/api-docs/**"),
		                antPathRequestMatcher("/swagger-resources/**"), antPathRequestMatcher("/webjars/**"))
		        .permitAll()
		        .requestMatchers(antPathRequestMatcher("/users/me/trainees", HttpMethod.POST.name()),
		                antPathRequestMatcher("/users/me/trainers", HttpMethod.POST.name()))
		        .permitAll().anyRequest().authenticated())
		        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
		        .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
		        .exceptionHandling(exception -> exception.accessDeniedHandler(accessDeniedHandler)
		                .authenticationEntryPoint(authenticationEntryPoint));

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
	        throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private AntPathRequestMatcher antPathRequestMatcher(String uri) {
		return new AntPathRequestMatcher(uri);
	}

	private AntPathRequestMatcher antPathRequestMatcher(String uri, String method) {
		return new AntPathRequestMatcher(uri, method);
	}
}
