package com.gymcrm.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.CreateUserCommand;
import com.gymcrm.domain.User;
import com.gymcrm.factory.UserFactory;
import com.gymcrm.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock private UserFactory userFactory;

  @InjectMocks private UserService userService;

  private CreateUserCommand validCommand;
  private User user;

  @BeforeEach
  void setUp() {
    validCommand = new CreateUserCommand();
    validCommand.setFirstName("John");
    validCommand.setLastName("Doe");

    user = new User();
    user.setId(UUID.randomUUID());
    user.setFirstName("John");
    user.setLastName("Doe");
  }

  @Test
  void createUser_shouldCreateUserSuccessfully() {
    when(userFactory.createFrom(validCommand)).thenReturn(user);

    User result = userService.createUser(validCommand);

    assertNotNull(result);
    assertEquals(user, result);
    verify(userFactory).createFrom(validCommand);
  }

  @Test
  void createUser_shouldThrowExceptionWhenFirstNameIsNull() {
    validCommand.setFirstName(null);
    assertThrows(IllegalArgumentException.class, () -> userService.createUser(validCommand));
  }

  @Test
  void createUser_shouldThrowExceptionWhenFirstNameIsBlank() {
    validCommand.setFirstName("   ");
    assertThrows(IllegalArgumentException.class, () -> userService.createUser(validCommand));
  }

  @Test
  void createUser_shouldThrowExceptionWhenLastNameIsNull() {
    validCommand.setLastName(null);
    assertThrows(IllegalArgumentException.class, () -> userService.createUser(validCommand));
  }

  @Test
  void createUser_shouldThrowExceptionWhenLastNameIsBlank() {
    validCommand.setLastName("   ");
    assertThrows(IllegalArgumentException.class, () -> userService.createUser(validCommand));
  }
}
