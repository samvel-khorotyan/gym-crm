package com.gymcrm.user.application.port.output;

import com.gymcrm.user.domain.UserType;

public interface AuthenticationPort {
  boolean userExistsByCredentials(String username, String password, UserType userType);
}
