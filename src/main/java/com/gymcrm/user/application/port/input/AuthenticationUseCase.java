package com.gymcrm.user.application.port.input;

public interface AuthenticationUseCase {
  boolean authenticateAdmin(String username, String password);

  boolean authenticateTrainer(String username, String password);

  boolean authenticateTrainee(String username, String password);
}
