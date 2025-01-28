package com.gymcrm.unit.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.user.application.AuthenticationService;
import com.gymcrm.user.application.port.output.AuthenticationPort;
import com.gymcrm.user.domain.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
  @Mock private AuthenticationPort authenticationPort;

  @InjectMocks private AuthenticationService authenticationService;

  private String validUsername;
  private String validPassword;

  @BeforeEach
  void setUp() {
    validUsername = "validUser";
    validPassword = "validPass";
  }

  @Test
  void authenticateAdmin_ShouldReturnTrue_WhenCredentialsAreValid() {
    when(authenticationPort.userExistsByCredentials(validUsername, validPassword, UserType.ADMIN))
        .thenReturn(true);

    boolean result = authenticationService.authenticateAdmin(validUsername, validPassword);

    assertTrue(result);
    verify(authenticationPort, times(1))
        .userExistsByCredentials(validUsername, validPassword, UserType.ADMIN);
  }

  @Test
  void authenticateAdmin_ShouldThrowUnauthorizedException_WhenCredentialsAreInvalid() {
    when(authenticationPort.userExistsByCredentials(validUsername, validPassword, UserType.ADMIN))
        .thenReturn(false);

    UnauthorizedException exception =
        assertThrows(
            UnauthorizedException.class,
            () -> authenticationService.authenticateAdmin(validUsername, validPassword));

    assertEquals(
        "Unauthorized access attempt for admin with username: validUser", exception.getMessage());
    verify(authenticationPort, times(1))
        .userExistsByCredentials(validUsername, validPassword, UserType.ADMIN);
  }

  @Test
  void authenticateTrainer_ShouldReturnTrue_WhenCredentialsAreValid() {
    when(authenticationPort.userExistsByCredentials(validUsername, validPassword, UserType.TRAINER))
        .thenReturn(true);

    boolean result = authenticationService.authenticateTrainer(validUsername, validPassword);

    assertTrue(result);
    verify(authenticationPort, times(1))
        .userExistsByCredentials(validUsername, validPassword, UserType.TRAINER);
  }

  @Test
  void authenticateTrainer_ShouldThrowUnauthorizedException_WhenCredentialsAreInvalid() {
    when(authenticationPort.userExistsByCredentials(validUsername, validPassword, UserType.TRAINER))
        .thenReturn(false);

    UnauthorizedException exception =
        assertThrows(
            UnauthorizedException.class,
            () -> authenticationService.authenticateTrainer(validUsername, validPassword));

    assertEquals(
        "Unauthorized access attempt for trainer with username: validUser", exception.getMessage());
    verify(authenticationPort, times(1))
        .userExistsByCredentials(validUsername, validPassword, UserType.TRAINER);
  }

  @Test
  void authenticateTrainee_ShouldReturnTrue_WhenCredentialsAreValid() {
    when(authenticationPort.userExistsByCredentials(validUsername, validPassword, UserType.TRAINEE))
        .thenReturn(true);

    boolean result = authenticationService.authenticateTrainee(validUsername, validPassword);

    assertTrue(result);
    verify(authenticationPort, times(1))
        .userExistsByCredentials(validUsername, validPassword, UserType.TRAINEE);
  }

  @Test
  void authenticateTrainee_ShouldThrowUnauthorizedException_WhenCredentialsAreInvalid() {
    when(authenticationPort.userExistsByCredentials(validUsername, validPassword, UserType.TRAINEE))
        .thenReturn(false);

    UnauthorizedException exception =
        assertThrows(
            UnauthorizedException.class,
            () -> authenticationService.authenticateTrainee(validUsername, validPassword));

    assertEquals(
        "Unauthorized access attempt for trainee with username: validUser", exception.getMessage());
    verify(authenticationPort, times(1))
        .userExistsByCredentials(validUsername, validPassword, UserType.TRAINEE);
  }
}
