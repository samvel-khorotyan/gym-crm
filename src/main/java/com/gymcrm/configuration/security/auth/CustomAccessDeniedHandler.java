package com.gymcrm.configuration.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
	        AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
		response.setContentType("application/json");

		Map<String, String> errorResponse = Map.of("error", "Forbidden", "message",
		        "You do not have permission to access this resource");

		new ObjectMapper().writeValue(response.getWriter(), errorResponse);
	}
}
