package com.gymcrm.unit.trainee.application.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TraineeNotFoundExceptionTest {
  @Test
  void constructor_ShouldSetMessageCorrectly() {
    String message = "Trainee not found";

    TraineeNotFoundException exception = new TraineeNotFoundException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  void by_ShouldReturnExceptionWithCorrectMessage_ById() {
    UUID id = UUID.randomUUID();

    TraineeNotFoundException exception = TraineeNotFoundException.by(id);

    assertEquals("Trainee not found by trainee ID: " + id, exception.getMessage());
  }
}
