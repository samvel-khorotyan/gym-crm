package com.gymcrm.unit.user.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.adapter.output.persistence.UserPersistenceRepository;
import com.gymcrm.user.adapter.output.persistence.UserRepository;
import com.gymcrm.user.application.exception.UserNotFoundException;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {
  @Mock private UserPersistenceRepository repository;

  @InjectMocks private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    UUID userId = UUID.randomUUID();
    user = new User(userId, "John", "Doe", "john-doe", "password123", true, UserType.TRAINEE);
  }

  @Test
  void save_ShouldSaveUser() {
    when(repository.save(user)).thenReturn(user);

    User savedUser = userRepository.save(user);

    assertNotNull(savedUser);
    assertEquals(user, savedUser);
    verify(repository, times(1)).save(user);
  }

  @Test
  void findDistinctUsernamesStartingWith_ShouldReturnListOfUsernames() {
    String baseUsername = "john";
    List<String> usernames = Arrays.asList("john-doe", "john-smith");
    when(repository.findDistinctUsernamesStartingWith(baseUsername)).thenReturn(usernames);

    List<String> result = userRepository.findDistinctUsernamesStartingWith(baseUsername);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertTrue(result.contains("john-doe"));
    verify(repository, times(1)).findDistinctUsernamesStartingWith(baseUsername);
  }

  @Test
  void findByUsername_ShouldReturnUser_WhenUserExists() {
    String username = "john-doe";
    when(repository.findByUsername(username)).thenReturn(Optional.of(user));

    User foundUser = userRepository.findByUsername(username);

    assertNotNull(foundUser);
    assertEquals(user, foundUser);
    verify(repository, times(1)).findByUsername(username);
  }

  @Test
  void findByUsername_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
    String username = "nonexistent";
    when(repository.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userRepository.findByUsername(username));
    verify(repository, times(1)).findByUsername(username);
  }

  @Test
  void findAll_ShouldReturnListOfUsers() {
    List<User> users =
        Arrays.asList(
            user,
            new User(
                UUID.randomUUID(),
                "Jane",
                "Doe",
                "jane-doe",
                "password123",
                true,
                UserType.TRAINER));
    when(repository.findAll()).thenReturn(users);

    List<User> result = userRepository.findAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(repository, times(1)).findAll();
  }

  @Test
  void userExistsByCredentials_ShouldReturnTrue_WhenUserExists() {
    String username = "john-doe";
    String password = "password123";
    UserType userType = UserType.TRAINEE;
    when(repository.existsByUsernameAndPasswordAndUserType(username, password, userType))
        .thenReturn(true);

    boolean exists = userRepository.userExistsByCredentials(username, password, userType);

    assertTrue(exists);
    verify(repository, times(1))
        .existsByUsernameAndPasswordAndUserType(username, password, userType);
  }

  @Test
  void userExistsByCredentials_ShouldReturnFalse_WhenUserDoesNotExist() {
    String username = "nonexistent";
    String password = "wrong-password";
    UserType userType = UserType.TRAINEE;
    when(repository.existsByUsernameAndPasswordAndUserType(username, password, userType))
        .thenReturn(false);

    boolean exists = userRepository.userExistsByCredentials(username, password, userType);

    assertFalse(exists);
    verify(repository, times(1))
        .existsByUsernameAndPasswordAndUserType(username, password, userType);
  }
}
