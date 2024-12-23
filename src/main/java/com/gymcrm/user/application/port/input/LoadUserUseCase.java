package com.gymcrm.user.application.port.input;

import com.gymcrm.user.domain.User;
import java.util.List;

public interface LoadUserUseCase {
  User loadUserByUsername(String username);

  List<User> loadAll();
}
