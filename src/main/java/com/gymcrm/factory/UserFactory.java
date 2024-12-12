package com.gymcrm.factory;

import com.gymcrm.command.CreateUserCommand;
import com.gymcrm.domain.User;
import com.gymcrm.util.UUIDGeneratorInterface;
import com.gymcrm.util.UserUtil;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
  private final UUIDGeneratorInterface uuidGeneratorInterface;

  public UserFactory(UUIDGeneratorInterface uuidGeneratorInterface) {
    this.uuidGeneratorInterface = uuidGeneratorInterface;
  }

  public User createFrom(CreateUserCommand command) {
    return new User(
        uuidGeneratorInterface.newUUID(),
        command.getFirstName(),
        command.getLastName(),
        UserUtil.generateUsername(command.getFirstName(), command.getLastName()),
        UserUtil.generatePassword(),
        true);
  }
}
