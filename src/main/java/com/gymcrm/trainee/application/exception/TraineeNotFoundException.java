package com.gymcrm.trainee.application.exception;

import com.gymcrm.common.exception.NotFoundException;
import java.util.UUID;

public class TraineeNotFoundException extends NotFoundException {
  public TraineeNotFoundException(String message) {
    super(message);
  }

  public static TraineeNotFoundException by(UUID id) {
    return new TraineeNotFoundException("Trainee not found by trainee ID: " + id);
  }
}
