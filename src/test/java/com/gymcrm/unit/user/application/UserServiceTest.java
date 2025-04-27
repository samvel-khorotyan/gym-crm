package com.gymcrm.unit.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.exception.InvalidPasswordException;
import com.gymcrm.user.application.UserService;
import com.gymcrm.user.application.exception.UserNotFoundException;
import com.gymcrm.user.application.factory.UserFactory;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.UpdatePasswordCommand;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.Collections;
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
	void createUserSuccess() {
		CreateUserCommand command = new CreateUserCommand("John", "Doe", UserType.TRAINER);
		UUID userId = UUID.randomUUID();
		User user = new User();
		user.setId(userId);

		when(passwordEncoder.encode(anyString())).thenReturn("encodedPass");
		when(userFactory.createFrom(command)).thenReturn(user);
		when(updateUserPort.save(user)).thenReturn(user);
		when(loadUserPort.findDistinctUsernamesStartingWith(anyString())).thenReturn(Collections.emptyList());

		User createdUser = userService.create(command);

		assertNotNull(createdUser);
		assertEquals(userId, createdUser.getId());
	}

	@Test
	void loadUserByUsernameSuccess() {
		User user = new User();
		user.setUsername("john.doe");

		when(loadUserPort.findByUsername("john.doe")).thenReturn(user);

		User loadedUser = userService.loadUserByUsername("john.doe");

		assertNotNull(loadedUser);
		assertEquals("john.doe", loadedUser.getUsername());
	}

	@Test
  void loadUserByUsernameNotFound() {
    when(loadUserPort.findByUsername("john.doe")).thenThrow(new UserNotFoundException("Not found"));

    assertThrows(UserNotFoundException.class, () -> userService.loadUserByUsername("john.doe"));
  }

	@Test
  void loadAllUsersSuccess() {
    when(loadUserPort.findAll()).thenReturn(List.of(new User(), new User()));

    List<User> users = userService.loadAll();

    assertEquals(2, users.size());
  }

	@Test
	void updatePasswordSuccess() {
		UpdatePasswordCommand command = new UpdatePasswordCommand("john.doe", "oldPass", "newPass");
		User user = new User();
		user.setPassword("encodedOldPass");

		when(loadUserPort.findByUsername("john.doe")).thenReturn(user);
		when(passwordEncoder.matches("oldPass", "encodedOldPass")).thenReturn(true);
		when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

		assertDoesNotThrow(() -> userService.updatePassword(command));
		verify(updateUserPort).save(user);
	}

	@Test
	void updatePasswordInvalidOldPassword() {
		UpdatePasswordCommand command = new UpdatePasswordCommand("john.doe", "wrongPass", "newPass");
		User user = new User();
		user.setPassword("encodedOldPass");

		when(loadUserPort.findByUsername("john.doe")).thenReturn(user);
		when(passwordEncoder.matches("wrongPass", "encodedOldPass")).thenReturn(false);

		InvalidPasswordException exception = assertThrows(InvalidPasswordException.class,
		        () -> userService.updatePassword(command));

		assertEquals("Current password is incorrect", exception.getMessage());

		verify(updateUserPort, never()).save(any(User.class));
	}

	@Test
  void generateUsernameException() {
    when(loadUserPort.findDistinctUsernamesStartingWith(anyString()))
        .thenThrow(new RuntimeException("DB Error"));

    RuntimeException ex =
        assertThrows(
            RuntimeException.class,
            () -> userService.create(new CreateUserCommand("John", "Doe", UserType.TRAINEE)));

    assertEquals("Failed to create user", ex.getMessage());
    assertNotNull(ex.getCause());
    assertEquals("Failed to generate username", ex.getCause().getMessage());
  }
}
