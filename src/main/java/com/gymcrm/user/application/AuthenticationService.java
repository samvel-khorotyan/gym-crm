package com.gymcrm.user.application;

import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.output.AuthenticationPort;
import com.gymcrm.user.domain.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationUseCase {
  private final AuthenticationPort authenticationPort;

  @Autowired
  public AuthenticationService(AuthenticationPort AuthenticationPort) {
    this.authenticationPort = AuthenticationPort;
  }

  @Override
  public boolean authenticateAdmin(String username, String password) {
    if (authenticationPort.userExistsByCredentials(username, password, UserType.ADMIN)) return true;
    throw new UnauthorizedException(
        "Unauthorized access attempt for admin with username: " + username);
  }

  @Override
  public boolean authenticateTrainer(String username, String password) {
    if (authenticationPort.userExistsByCredentials(username, password, UserType.TRAINER))
      return true;
    throw new UnauthorizedException(
        "Unauthorized access attempt for trainer with username: " + username);
  }

  @Override
  public boolean authenticateTrainee(String username, String password) {
    if (authenticationPort.userExistsByCredentials(username, password, UserType.TRAINEE))
      return true;
    throw new UnauthorizedException(
        "Unauthorized access attempt for trainee with username: " + username);
  }
}
