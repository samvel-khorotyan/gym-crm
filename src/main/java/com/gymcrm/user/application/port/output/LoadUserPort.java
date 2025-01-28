package com.gymcrm.user.application.port.output;

import com.gymcrm.user.domain.User;
import java.util.List;

public interface LoadUserPort {
  List<String> findDistinctUsernamesStartingWith(String baseUsername);

  User findByUsername(String username);

  List<User> findAll();
}
