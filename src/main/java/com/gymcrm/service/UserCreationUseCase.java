package com.gymcrm.service;

import com.gymcrm.command.CreateUserCommand;
import com.gymcrm.domain.User;

public interface UserCreationUseCase {
  User createUser(CreateUserCommand command);
}
