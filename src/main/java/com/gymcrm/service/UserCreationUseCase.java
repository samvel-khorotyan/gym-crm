package com.gymcrm.service;

import com.gymcrm.domain.User;

public interface UserCreationUseCase {
  User createUser(String firstName, String lastName);
}
