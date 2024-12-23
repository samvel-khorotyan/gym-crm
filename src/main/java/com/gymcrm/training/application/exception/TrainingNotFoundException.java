package com.gymcrm.training.application.exception;

import com.gymcrm.common.exception.NotFoundException;
import java.util.UUID;

public class TrainingNotFoundException extends NotFoundException {
  public TrainingNotFoundException(String message) {
    super(message);
  }

  public static TrainingNotFoundException by(UUID id) {
    return new TrainingNotFoundException("Training not found by training ID: " + id);
  }
}
