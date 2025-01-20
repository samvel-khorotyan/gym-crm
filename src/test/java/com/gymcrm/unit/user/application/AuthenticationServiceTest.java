package com.gymcrm.unit.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.application.AuthenticationService;
import com.gymcrm.user.application.port.output.AuthenticationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
	@Mock
	private AuthenticationPort authenticationPort;

	@InjectMocks
	private AuthenticationService authenticationService;

	@Test
	void authenticate_ShouldSucceed_WhenCredentialsAreValid() {
		String username = "validUser";
		String password = "validPassword";
		MDC.put("transactionId", "12345");
		when(authenticationPort.userExistsByCredentials(username, password)).thenReturn(true);

		assertDoesNotThrow(() -> authenticationService.authenticate(username, password));
		verify(authenticationPort, times(1)).userExistsByCredentials(username, password);
	}

	@Test
	void authenticate_ShouldThrowRuntimeException_WhenAuthenticationPortFails() {
		String username = "userWithError";
		String password = "password";
		MDC.put("transactionId", "12345");
		when(authenticationPort.userExistsByCredentials(username, password))
		        .thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> authenticationService.authenticate(username, password));
		assertEquals("Authentication process failed.", exception.getMessage());
		verify(authenticationPort, times(1)).userExistsByCredentials(username, password);
	}
}
