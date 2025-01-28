package com.gymcrm.unit.user.application.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.user.application.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;

class UserNotFoundExceptionTest {
  @Test
  void constructor_ShouldSetMessageCorrectly() {
    String message = "User not found";

    UserNotFoundException exception = new UserNotFoundException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  void by_ShouldReturnExceptionWithCorrectMessage() {
    String username = "john.doe";

    UserNotFoundException exception = UserNotFoundException.by(username);

    assertEquals("User not found by username: john.doe", exception.getMessage());
  }
}
