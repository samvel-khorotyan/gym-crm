package com.gymcrm.user.application;

import com.gymcrm.user.application.factory.UserFactory;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.LoadUserUseCase;
import com.gymcrm.user.application.port.input.UserCreationUseCase;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.util.UserUtil;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserCreationUseCase, LoadUserUseCase {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserFactory userFactory;
  private final UpdateUserPort updateUserPort;
  private final LoadUserPort loadUserPort;

  @Autowired
  public UserService(
      UserFactory userFactory, UpdateUserPort updateUserPort, LoadUserPort loadUserPort) {
    this.userFactory = userFactory;
    this.updateUserPort = updateUserPort;
    this.loadUserPort = loadUserPort;
  }

  @Override
  public User create(CreateUserCommand command) {
    logger.debug("Validating user creation command");

    if (command.getFirstName() == null
        || command.getFirstName().isBlank()
        || command.getLastName() == null
        || command.getLastName().isBlank()) {
      throw new IllegalArgumentException("First name and last name cannot be null or empty");
    }

    logger.debug("Creating user for: {} {}", command.getFirstName(), command.getLastName());

    command.setUsername(generateUsername(command.getFirstName(), command.getLastName()));
    command.setPassword(UserUtil.generatePassword());

    return updateUserPort.save(userFactory.createFrom(command));
  }

  private String generateUsername(String firstName, String lastName) {
    String baseUsername = UserUtil.getBaseUsername(firstName, lastName);
    var usernames = loadUserPort.findDistinctUsernamesStartingWith(baseUsername);
    return UserUtil.generateUniqueUsername(new HashSet<>(usernames), baseUsername);
  }

  @Override
  public User loadUserByUsername(String username) {
    return loadUserPort.findByUsername(username);
  }

  @Override
  public List<User> loadAll() {
    return loadUserPort.findAll();
  }
}
