package com.gymcrm.unit.configuration.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.configuration.security.auth.CustomAuthenticationEntryPoint;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private AuthenticationException authException;

	@InjectMocks
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Test
	void shouldReturn401UnauthorizedWithJsonResponse() throws Exception {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		when(response.getWriter()).thenReturn(printWriter);

		customAuthenticationEntryPoint.commence(request, response, authException);

		verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response, times(1)).setContentType("application/json");

		printWriter.flush();
		String jsonResponse = stringWriter.toString();

		Map<String, String> expectedResponse = Map.of("error", "Unauthorized", "message",
		        "User authentication failed or credentials are missing");

		String expectedJson = new ObjectMapper().writeValueAsString(expectedResponse);
		assertEquals(expectedJson, jsonResponse.trim());
	}
}
