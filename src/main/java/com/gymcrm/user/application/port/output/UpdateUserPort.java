package com.gymcrm.user.application.port.output;

import com.gymcrm.user.domain.User;

public interface UpdateUserPort {
  User save(User user);
}
