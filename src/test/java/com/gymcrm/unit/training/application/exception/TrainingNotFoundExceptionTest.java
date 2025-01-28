package com.gymcrm.unit.training.application.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.training.application.exception.TrainingNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TrainingNotFoundExceptionTest {
  @Test
  void constructor_ShouldSetMessageCorrectly() {
    String message = "Training not found";

    TrainingNotFoundException exception = new TrainingNotFoundException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  void by_ShouldReturnExceptionWithCorrectMessage_ById() {
    UUID id = UUID.randomUUID();

    TrainingNotFoundException exception = TrainingNotFoundException.by(id);

    assertEquals("Training not found by training ID: " + id, exception.getMessage());
  }
}
