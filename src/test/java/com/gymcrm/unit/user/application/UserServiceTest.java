package com.gymcrm.unit.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.application.UserService;
import com.gymcrm.user.application.factory.UserFactory;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserFactory userFactory;

  @Mock private UpdateUserPort updateUserPort;

  @Mock private LoadUserPort loadUserPort;

  @InjectMocks private UserService userService;

  private CreateUserCommand validCommand;

  @BeforeEach
  void setUp() {
    validCommand = new CreateUserCommand("John", "Doe");
    validCommand.setUserType(UserType.TRAINEE);
  }

  @Test
  void create_ShouldReturnUser_WhenValidCommandIsProvided() {
    User mockUser = new User();
    mockUser.setFirstName("John");
    mockUser.setLastName("Doe");
    mockUser.setUsername("john.doe");
    mockUser.setPassword("randomPass");

    when(userFactory.createFrom(any(CreateUserCommand.class))).thenReturn(mockUser);
    when(updateUserPort.save(mockUser)).thenReturn(mockUser);
    when(loadUserPort.findDistinctUsernamesStartingWith("john.doe")).thenReturn(new ArrayList<>());

    User createdUser = userService.create(validCommand);

    assertNotNull(createdUser);
    assertEquals("John", createdUser.getFirstName());
    assertEquals("Doe", createdUser.getLastName());
    verify(userFactory, times(1)).createFrom(any(CreateUserCommand.class));
    verify(updateUserPort, times(1)).save(any(User.class));
  }

  @Test
  void create_ShouldThrowRuntimeExceptionWithIllegalArgumentException_WhenFirstNameIsNull() {
    validCommand.setFirstName(null);

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> userService.create(validCommand));

    assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    assertEquals(
        "First name and last name cannot be null or empty", exception.getCause().getMessage());
  }

  @Test
  void create_ShouldThrowRuntimeExceptionWithIllegalArgumentException_WhenLastNameIsBlank() {
    validCommand.setLastName("   ");

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> userService.create(validCommand));

    assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    assertEquals(
        "First name and last name cannot be null or empty", exception.getCause().getMessage());
  }

  @Test
  void create_ShouldGenerateUsernameAndPassword_WhenValidInputProvided() {
    validCommand.setFirstName("John");
    validCommand.setLastName("Doe");
    User mockUser = new User();
    when(userFactory.createFrom(any(CreateUserCommand.class))).thenReturn(mockUser);
    when(updateUserPort.save(any(User.class))).thenReturn(mockUser);
    when(loadUserPort.findDistinctUsernamesStartingWith("john.doe")).thenReturn(new ArrayList<>());

    User createdUser = userService.create(validCommand);

    assertNotNull(createdUser);
    verify(userFactory, times(1)).createFrom(validCommand);
    verify(updateUserPort, times(1)).save(mockUser);
  }

  @Test
  void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
    User mockUser = new User();
    mockUser.setUsername("john.doe");

    when(loadUserPort.findByUsername("john.doe")).thenReturn(mockUser);

    User loadedUser = userService.loadUserByUsername("john.doe");

    assertNotNull(loadedUser);
    assertEquals("john.doe", loadedUser.getUsername());
    verify(loadUserPort, times(1)).findByUsername("john.doe");
  }

  @Test
  void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
    when(loadUserPort.findByUsername("unknown.user"))
        .thenThrow(new RuntimeException("User not found"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> userService.loadUserByUsername("unknown.user"));

    assertEquals("Failed to fetch user by username", exception.getMessage());
    verify(loadUserPort, times(1)).findByUsername("unknown.user");
  }

  @Test
  void loadAll_ShouldReturnListOfUsers() {
    User user1 = new User();
    user1.setUsername("john.doe");
    User user2 = new User();
    user2.setUsername("jane.doe");

    when(loadUserPort.findAll()).thenReturn(List.of(user1, user2));

    List<User> users = userService.loadAll();

    assertNotNull(users);
    assertEquals(2, users.size());
    verify(loadUserPort, times(1)).findAll();
  }

  @Test
  void loadAll_ShouldThrowException_WhenFetchFails() {
    when(loadUserPort.findAll()).thenThrow(new RuntimeException("Database error"));

    RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.loadAll());

    assertEquals("Failed to fetch all users", exception.getMessage());
    verify(loadUserPort, times(1)).findAll();
  }
}
