package com.gymcrm.user.application.port.output;

public interface AuthenticationPort {
  boolean userExistsByCredentials(String username, String password);
}
