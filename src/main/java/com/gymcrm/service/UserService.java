package com.gymcrm.service;

import com.gymcrm.domain.User;
import com.gymcrm.util.UUIDGeneratorInterface;
import com.gymcrm.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserCreationUseCase {
  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UUIDGeneratorInterface uuidGenerator;

  @Autowired
  public UserService(UUIDGeneratorInterface uuidGenerator) {
    this.uuidGenerator = uuidGenerator;
  }

  @Override
  public User createUser(String firstName, String lastName) {
    logger.info("Starting user creation process");

    if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
      logger.error("Invalid input: first name or last name is null/empty");
      throw new IllegalArgumentException("First name and last name cannot be null or empty");
    }

    User user = new User();
    user.setId(uuidGenerator.newUUID());
    user.setFirstName(firstName.trim());
    user.setLastName(lastName.trim());
    user.setActive(true);

    String username = UserUtil.generateUsername(firstName, lastName);
    String password = UserUtil.generatePassword();

    user.setUsername(username);
    user.setPassword(password);

    logger.info("User created successfully with ID: {} and username: {}", user.getId(), username);
    return user;
  }
}
