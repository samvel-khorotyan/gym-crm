package com.gymcrm.user.application.port.input;

import com.gymcrm.user.domain.User;

public interface UserCreationUseCase {
  User create(CreateUserCommand command);
}
