package com.gymcrm.service;

import com.gymcrm.command.CreateUserCommand;
import com.gymcrm.domain.User;
import com.gymcrm.factory.UserFactory;
import com.gymcrm.usecase.UserCreationUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserCreationUseCase {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserFactory userFactory;

  @Autowired
  public UserService(UserFactory userFactory) {
    this.userFactory = userFactory;
  }

  @Override
  public User createUser(CreateUserCommand command) {
    logger.debug("Validating user creation command");

    if (command.getFirstName() == null
        || command.getFirstName().isBlank()
        || command.getLastName() == null
        || command.getLastName().isBlank()) {
      throw new IllegalArgumentException("First name and last name cannot be null or empty");
    }

    logger.debug("Creating user for: {} {}", command.getFirstName(), command.getLastName());
    return userFactory.createFrom(command);
  }
}
