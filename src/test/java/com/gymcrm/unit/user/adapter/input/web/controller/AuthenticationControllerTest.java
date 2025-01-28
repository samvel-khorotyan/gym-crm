package com.gymcrm.unit.user.adapter.input.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.adapter.input.web.controller.AuthenticationController;
import com.gymcrm.user.adapter.input.web.request.LoginUpdateRequest;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.UserUpdateUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
	@Mock
	private AuthenticationUseCase authenticationUseCase;

	@Mock
	private UserUpdateUseCase userUpdateUseCase;

	@InjectMocks
	private AuthenticationController authenticationController;

	@Test
	void login_ShouldAuthenticateUser_WhenCredentialsAreValid() {
		String username = "testUser";
		String password = "password123";

		assertDoesNotThrow(() -> authenticationController.login(username, password));

		verify(authenticationUseCase, times(1)).authenticate(username, password);
	}

	@Test
	void login_ShouldThrowUnauthorized_WhenAuthenticationFails() {
		String username = "testUser";
		String password = "invalidPassword";

		doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")).when(authenticationUseCase)
		        .authenticate(username, password);

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
		        () -> authenticationController.login(username, password));

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
		verify(authenticationUseCase, times(1)).authenticate(username, password);
	}

	@Test
	void updateLoginDetails_ShouldUpdatePassword_WhenRequestIsValid() {
		LoginUpdateRequest request = new LoginUpdateRequest("testUser", "oldPassword123", "newPassword123");

		assertDoesNotThrow(() -> authenticationController.updateLoginDetails(request));

		verify(userUpdateUseCase, times(1)).updatePassword(request.toCommand());
	}

	@Test
	void updateLoginDetails_ShouldThrowUnauthorized_WhenUpdateFails() {
		LoginUpdateRequest request = new LoginUpdateRequest("testUser", "oldPassword123", "newPassword123");

		doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized update")).when(userUpdateUseCase)
		        .updatePassword(request.toCommand());

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
		        () -> authenticationController.updateLoginDetails(request));

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
		verify(userUpdateUseCase, times(1)).updatePassword(request.toCommand());
	}
}
