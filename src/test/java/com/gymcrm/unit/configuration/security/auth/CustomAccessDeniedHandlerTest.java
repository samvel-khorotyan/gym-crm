package com.gymcrm.unit.configuration.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.configuration.security.auth.CustomAccessDeniedHandler;
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
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@InjectMocks
	private CustomAccessDeniedHandler customAccessDeniedHandler;

	@Test
	void shouldReturn403ForbiddenWithJsonResponse() throws Exception {
		AccessDeniedException accessDeniedException = new AccessDeniedException("Access is denied");

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		when(response.getWriter()).thenReturn(printWriter);

		customAccessDeniedHandler.handle(request, response, accessDeniedException);

		verify(response, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(response, times(1)).setContentType("application/json");

		printWriter.flush();
		String jsonResponse = stringWriter.toString();

		Map<String, String> expectedResponse = Map.of("error", "Forbidden", "message",
		        "You do not have permission to access this resource");

		String expectedJson = new ObjectMapper().writeValueAsString(expectedResponse);
		assertEquals(expectedJson, jsonResponse.trim());
	}
}
