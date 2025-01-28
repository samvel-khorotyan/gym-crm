package com.gymcrm.unit.trainingtype.application.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.trainingtype.application.exception.TrainingTypeNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TrainingTypeNotFoundExceptionTest {
  @Test
  void constructor_ShouldSetMessageCorrectly() {
    String message = "Training type not found";

    TrainingTypeNotFoundException exception = new TrainingTypeNotFoundException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  void by_ShouldReturnExceptionWithCorrectMessage_ById() {
    UUID id = UUID.randomUUID();

    TrainingTypeNotFoundException exception = TrainingTypeNotFoundException.by(id);

    assertEquals("Training type not found by training type ID: " + id, exception.getMessage());
  }

  @Test
  void by_ShouldReturnExceptionWithCorrectMessage_ByName() {
    String trainingTypeName = "Wrestling";

    TrainingTypeNotFoundException exception = TrainingTypeNotFoundException.by(trainingTypeName);

    assertEquals(
        "Training type not found by training type name: " + trainingTypeName,
        exception.getMessage());
  }
}
