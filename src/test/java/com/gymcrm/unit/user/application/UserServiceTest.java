package com.gymcrm.unit.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.user.application.UserService;
import com.gymcrm.user.application.exception.UserNotFoundException;
import com.gymcrm.user.application.factory.UserFactory;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.UpdatePasswordCommand;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import com.gymcrm.util.UserUtil;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserFactory userFactory;

	@Mock
	private UpdateUserPort updateUserPort;

	@Mock
	private LoadUserPort loadUserPort;

	@Mock
	private AuthenticationUseCase authenticationUseCase;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {
		// Any necessary setup before each test
	}

	@Test
	void create_ShouldReturnUser_WhenValidCommandProvided() {
		CreateUserCommand command = new CreateUserCommand("John", "Doe", UserType.TRAINER);
		String generatedUsername = "john.doe";
		String generatedPassword = "securePassword123";
		User user = new User(UUID.randomUUID(), "John", "Doe", generatedUsername, generatedPassword, true,
		        UserType.TRAINER);

		MockedStatic<UserUtil> mockedUtil = mockStatic(UserUtil.class);
		mockedUtil.when(() -> UserUtil.getBaseUsername(command.getFirstName(), command.getLastName()))
		        .thenReturn(generatedUsername);
		mockedUtil.when(() -> UserUtil.generateUniqueUsername(anySet(), eq(generatedUsername)))
		        .thenReturn(generatedUsername);
		mockedUtil.when(UserUtil::generatePassword).thenReturn(generatedPassword);

		when(loadUserPort.findDistinctUsernamesStartingWith(generatedUsername)).thenReturn(List.of());
		when(userFactory.createFrom(command)).thenReturn(user);
		when(updateUserPort.save(user)).thenReturn(user);

		User result = userService.create(command);

		assertNotNull(result);
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		assertEquals(generatedUsername, result.getUsername());
		assertEquals(generatedPassword, result.getPassword());
		assertEquals(UserType.TRAINER, result.getUserType());
		verify(updateUserPort, times(1)).save(user);
	}

	@Test
	void create_ShouldThrowException_WhenFirstNameIsNull() {
		CreateUserCommand command = new CreateUserCommand(null, "Doe", UserType.TRAINER);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.create(command));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
		assertEquals("First name and last name cannot be null or empty", exception.getCause().getMessage());
		verifyNoInteractions(updateUserPort, userFactory);
	}

	@Test
	void create_ShouldThrowException_WhenLastNameIsBlank() {
		CreateUserCommand command = new CreateUserCommand("John", "   ", UserType.TRAINER);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.create(command));
		assertInstanceOf(IllegalArgumentException.class, exception.getCause());
		assertEquals("First name and last name cannot be null or empty", exception.getCause().getMessage());
		verifyNoInteractions(updateUserPort, userFactory);
	}

	@Test
	void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
		String username = "john.doe";
		User mockUser = new User(UUID.randomUUID(), "John", "Doe", username, "password123", true, null);
		when(loadUserPort.findByUsername(username)).thenReturn(mockUser);

		User result = userService.loadUserByUsername(username);

		assertNotNull(result);
		assertEquals(username, result.getUsername());
		verify(loadUserPort, times(1)).findByUsername(username);
	}

	@Test
	void loadUserByUsername_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
		String username = "non-existent-user";
		when(loadUserPort.findByUsername(username)).thenThrow(new UserNotFoundException("User not found"));

		UserNotFoundException exception = assertThrows(UserNotFoundException.class,
		        () -> userService.loadUserByUsername(username));
		assertEquals("User not found", exception.getMessage());
		verify(loadUserPort, times(1)).findByUsername(username);
	}

	@Test
	void loadUserByUsername_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
		String username = "john.doe";
		when(loadUserPort.findByUsername(username)).thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> userService.loadUserByUsername(username));
		assertEquals("Failed to fetch user by username", exception.getMessage());
		verify(loadUserPort, times(1)).findByUsername(username);
	}

	@Test
	void loadAll_ShouldReturnListOfUsers_WhenUsersExist() {
		User user1 = new User(UUID.randomUUID(), "John", "Doe", "john.doe", "password", true, UserType.TRAINER);
		User user2 = new User(UUID.randomUUID(), "Jane", "Smith", "jane.smith", "password", true, UserType.TRAINEE);
		when(loadUserPort.findAll()).thenReturn(List.of(user1, user2));

		List<User> users = userService.loadAll();

		assertNotNull(users);
		assertEquals(2, users.size());
		assertEquals("John", users.get(0).getFirstName());
		assertEquals("Jane", users.get(1).getFirstName());
		verify(loadUserPort, times(1)).findAll();
	}

	@Test
  void loadAll_ShouldReturnEmptyList_WhenNoUsersExist() {
    when(loadUserPort.findAll()).thenReturn(List.of());

    List<User> users = userService.loadAll();

    assertNotNull(users);
    assertTrue(users.isEmpty());
    verify(loadUserPort, times(1)).findAll();
  }

	@Test
  void loadAll_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
    when(loadUserPort.findAll()).thenThrow(new RuntimeException("Database error"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.loadAll());
    assertEquals("Failed to fetch all users", exception.getMessage());
    verify(loadUserPort, times(1)).findAll();
  }

	@Test
	void updatePassword_ShouldUpdatePassword_WhenCommandIsValid() {
		UpdatePasswordCommand command = new UpdatePasswordCommand("john.doe", "oldPassword", "newPassword");
		User user = new User(UUID.randomUUID(), "John", "Doe", "john.doe", "oldPassword", true, UserType.TRAINER);
		when(loadUserPort.findByUsername(command.getUsername())).thenReturn(user);
		doNothing().when(authenticationUseCase).authenticate(command.getUsername(), command.getOldPassword());
		when(updateUserPort.save(user)).thenReturn(user);

		userService.updatePassword(command);

		assertEquals("newPassword", user.getPassword());
		verify(authenticationUseCase, times(1)).authenticate(command.getUsername(), command.getOldPassword());
		verify(loadUserPort, times(1)).findByUsername(command.getUsername());
		verify(updateUserPort, times(1)).save(user);
	}

	@Test
	void updatePassword_ShouldThrowUnauthorizedException_WhenAuthenticationFails() {
		UpdatePasswordCommand command = new UpdatePasswordCommand("john.doe", "wrongPassword", "newPassword");
		doThrow(new UnauthorizedException("Invalid credentials")).when(authenticationUseCase)
		        .authenticate(command.getUsername(), command.getOldPassword());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
		        () -> userService.updatePassword(command));
		assertEquals("Invalid credentials", exception.getMessage());
		verify(authenticationUseCase, times(1)).authenticate(command.getUsername(), command.getOldPassword());
		verifyNoInteractions(loadUserPort, updateUserPort);
	}

	@Test
	void updatePassword_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
		UpdatePasswordCommand command = new UpdatePasswordCommand("non-existent-user", "oldPassword", "newPassword");
		when(loadUserPort.findByUsername(command.getUsername())).thenThrow(new UserNotFoundException("User not found"));

		UserNotFoundException exception = assertThrows(UserNotFoundException.class,
		        () -> userService.updatePassword(command));
		assertEquals("User not found", exception.getMessage());
		verify(authenticationUseCase, times(1)).authenticate(command.getUsername(), command.getOldPassword());
		verify(loadUserPort, times(1)).findByUsername(command.getUsername());
		verifyNoInteractions(updateUserPort);
	}

	@Test
	void updatePassword_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
		UpdatePasswordCommand command = new UpdatePasswordCommand("john.doe", "oldPassword", "newPassword");
		User user = new User(UUID.randomUUID(), "John", "Doe", "john.doe", "oldPassword", true, UserType.TRAINER);
		when(loadUserPort.findByUsername(command.getUsername())).thenReturn(user);
		doNothing().when(authenticationUseCase).authenticate(command.getUsername(), command.getOldPassword());
		doThrow(new RuntimeException("Database error")).when(updateUserPort).save(user);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updatePassword(command));
		assertEquals("Unexpected error occurred. Please contact support.", exception.getMessage());
		verify(authenticationUseCase, times(1)).authenticate(command.getUsername(), command.getOldPassword());
		verify(loadUserPort, times(1)).findByUsername(command.getUsername());
		verify(updateUserPort, times(1)).save(user);
	}
}
