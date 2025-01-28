package com.gymcrm.unit.user.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.adapter.output.persistence.UserPersistenceRepository;
import com.gymcrm.user.adapter.output.persistence.UserRepository;
import com.gymcrm.user.application.exception.UserNotFoundException;
import com.gymcrm.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
	@Mock
	private UserPersistenceRepository userPersistenceRepository;

	@InjectMocks
	private UserRepository userRepository;

	@Test
	void save_ShouldSaveUser_WhenValidUserProvided() {
		User user = new User();
		when(userPersistenceRepository.save(user)).thenReturn(user);

		User savedUser = userRepository.save(user);

		assertEquals(user, savedUser);
		verify(userPersistenceRepository, times(1)).save(user);
	}

	@Test
	void findDistinctUsernamesStartingWith_ShouldReturnUsernames_WhenBaseUsernameProvided() {
		String baseUsername = "john";
		List<String> usernames = List.of("john_doe", "john_smith");
		when(userPersistenceRepository.findDistinctUsernamesStartingWith(baseUsername)).thenReturn(usernames);

		List<String> result = userRepository.findDistinctUsernamesStartingWith(baseUsername);

		assertEquals(usernames.size(), result.size());
		assertTrue(result.contains("john_doe"));
		assertTrue(result.contains("john_smith"));
		verify(userPersistenceRepository, times(1)).findDistinctUsernamesStartingWith(baseUsername);
	}

	@Test
	void findByUsername_ShouldReturnUser_WhenUsernameExists() {
		String username = "john.doe";
		User user = new User();
		when(userPersistenceRepository.findByUsername(username)).thenReturn(Optional.of(user));

		User foundUser = userRepository.findByUsername(username);

		assertEquals(user, foundUser);
		verify(userPersistenceRepository, times(1)).findByUsername(username);
	}

	@Test
	void findByUsername_ShouldThrowException_WhenUsernameNotFound() {
		String username = "nonexistent";
		when(userPersistenceRepository.findByUsername(username)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> userRepository.findByUsername(username));
		verify(userPersistenceRepository, times(1)).findByUsername(username);
	}

	@Test
	void findAll_ShouldReturnAllUsers() {
		List<User> users = List.of(new User(), new User());
		when(userPersistenceRepository.findAll()).thenReturn(users);

		List<User> result = userRepository.findAll();

		assertEquals(users.size(), result.size());
		verify(userPersistenceRepository, times(1)).findAll();
	}

	@Test
	void userExistsByCredentials_ShouldReturnTrue_WhenUserExists() {
		String username = "john.doe";
		String password = "password123";
		when(userPersistenceRepository.existsByUsernameAndPassword(username, password)).thenReturn(true);

		boolean exists = userRepository.userExistsByCredentials(username, password);

		assertTrue(exists);
		verify(userPersistenceRepository, times(1)).existsByUsernameAndPassword(username, password);
	}

	@Test
	void userExistsByCredentials_ShouldReturnFalse_WhenUserDoesNotExist() {
		String username = "nonexistent";
		String password = "password123";
		when(userPersistenceRepository.existsByUsernameAndPassword(username, password)).thenReturn(false);

		boolean exists = userRepository.userExistsByCredentials(username, password);

		assertFalse(exists);
		verify(userPersistenceRepository, times(1)).existsByUsernameAndPassword(username, password);
	}
}
