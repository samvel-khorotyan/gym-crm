package com.gymcrm.user.application.exception;

import com.gymcrm.common.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
  public UserNotFoundException(String message) {
    super(message);
  }

  public static UserNotFoundException by(String username) {
    return new UserNotFoundException("User not found by username: " + username);
  }
}
