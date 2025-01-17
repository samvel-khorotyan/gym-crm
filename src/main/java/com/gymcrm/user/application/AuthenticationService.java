package com.gymcrm.user.application;

import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.output.AuthenticationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationUseCase {
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

  private final AuthenticationPort authenticationPort;

  @Autowired
  public AuthenticationService(AuthenticationPort AuthenticationPort) {
    this.authenticationPort = AuthenticationPort;
  }

  @Override
  public void authenticate(String username, String password) {
    if (authenticationPort.userExistsByCredentials(username, password)) return;
    logger.warn("Unauthorized access attempt for username: {}", username);
    throw new UnauthorizedException("Authentication failed. Please verify your credentials.");
  }
}
