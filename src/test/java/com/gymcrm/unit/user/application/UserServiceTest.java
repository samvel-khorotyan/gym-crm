package com.gymcrm.unit.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.application.UserService;
import com.gymcrm.user.application.exception.UserNotFoundException;
import com.gymcrm.user.application.factory.UserFactory;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import com.gymcrm.util.UserUtil;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@Mock
	private UserFactory userFactory;

	@Mock
	private UpdateUserPort updateUserPort;

	@Mock
	private LoadUserPort loadUserPort;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void create_ShouldReturnUser_WhenValidCommandProvided() {
		CreateUserCommand command = new CreateUserCommand("John", "Doe", UserType.TRAINER);
		String generatedUsername = UserUtil.getBaseUsername(command.getFirstName(), command.getLastName());
		String encodedPassword = "EncodedPassword123";

		User user = new User(UUID.randomUUID(), "John", "Doe", generatedUsername, encodedPassword, true,
		        UserType.TRAINER);

		when(loadUserPort.findDistinctUsernamesStartingWith(generatedUsername)).thenReturn(List.of());
		when(userFactory.createFrom(command)).thenReturn(user);
		when(updateUserPort.save(user)).thenReturn(user);
		when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);

		User result = userService.create(command);

		assertNotNull(result);
		assertEquals("John", result.getFirstName());
		assertEquals("Doe", result.getLastName());
		assertEquals(generatedUsername, result.getUsername());
		assertEquals(encodedPassword, result.getPassword());
		assertEquals(UserType.TRAINER, result.getUserType());

		verify(updateUserPort, times(1)).save(user);
		verify(passwordEncoder, times(1)).encode(anyString()); // Ստուգում ենք, որ passwordEncoder-ը կանչվել է
	}

	@Test
	void create_ShouldThrowException_WhenFirstNameIsNull() {
		CreateUserCommand command = new CreateUserCommand(null, "Doe", UserType.TRAINER);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.create(command));
		assertTrue(exception.getCause() instanceof IllegalArgumentException);
		assertEquals("First name and last name cannot be null or empty", exception.getCause().getMessage());
		verifyNoInteractions(updateUserPort, userFactory);
	}

	@Test
	void create_ShouldThrowException_WhenLastNameIsBlank() {
		CreateUserCommand command = new CreateUserCommand("John", " ", UserType.TRAINER);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.create(command));
		assertTrue(exception.getCause() instanceof IllegalArgumentException);
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
}
