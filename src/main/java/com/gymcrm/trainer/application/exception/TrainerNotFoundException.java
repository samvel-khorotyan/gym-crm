package com.gymcrm.trainer.application.exception;

import com.gymcrm.common.exception.NotFoundException;
import java.util.UUID;

public class TrainerNotFoundException extends NotFoundException {
  public TrainerNotFoundException(String message) {
    super(message);
  }

  public static TrainerNotFoundException by(UUID id) {
    return new TrainerNotFoundException("Trainer not found by trainer ID: " + id);
  }
}
