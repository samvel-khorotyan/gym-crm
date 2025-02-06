package com.gymcrm.configuration.security.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.util.JwtUtil;
import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		setFilterProcessesUrl("/users/me/login");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
	        throws AuthenticationException {
		try {
			Map<String, String> credentials = new ObjectMapper().readValue(request.getInputStream(),
			        new TypeReference<>() {
			        });
			String username = credentials.get("username");
			String password = credentials.get("password");

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new RuntimeException("Failed to read authentication request", e);
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
	        Authentication authResult) throws IOException {
		UserDetails userDetails = (UserDetails) authResult.getPrincipal();
		String role = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
		        .orElse("ROLE_USER");

		String token = jwtUtil.generateToken(userDetails.getUsername(), role);

		response.setContentType("application/json");
		response.getWriter().write("{\"token\": \"" + token + "\"}");
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
	        AuthenticationException failed) throws IOException {
		logger.debug("Authentication failed: " + failed.getMessage());

		int status = HttpServletResponse.SC_UNAUTHORIZED;

		if (failed instanceof LockedException) {
			status = HttpServletResponse.SC_FORBIDDEN;
			logger.debug("User is blocked - returning status: " + status);
		}

		response.setStatus(status);
		response.setContentType("application/json");

		Map<String, String> errorResponse = Map.of("error",
		        status == HttpServletResponse.SC_UNAUTHORIZED ? "Unauthorized" : "Forbidden", "message",
		        failed.getMessage());

		new ObjectMapper().writeValue(response.getWriter(), errorResponse);
	}
}
