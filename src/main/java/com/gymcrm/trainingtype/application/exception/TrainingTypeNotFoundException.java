package com.gymcrm.trainingtype.application.exception;

import com.gymcrm.common.exception.NotFoundException;
import java.util.UUID;

public class TrainingTypeNotFoundException extends NotFoundException {
  public TrainingTypeNotFoundException(String message) {
    super(message);
  }

  public static TrainingTypeNotFoundException by(UUID id) {
    return new TrainingTypeNotFoundException("Training type not found by training type ID: " + id);
  }

  public static TrainingTypeNotFoundException by(String trainingTypeName) {
    return new TrainingTypeNotFoundException(
        "Training type not found by training type name: " + trainingTypeName);
  }
}
