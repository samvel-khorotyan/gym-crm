package com.gymcrm.unit.trainer.application.exception;

import static org.junit.jupiter.api.Assertions.*;

import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TrainerNotFoundExceptionTest {
  @Test
  void constructor_ShouldSetMessageCorrectly() {
    String message = "Trainer not found";

    TrainerNotFoundException exception = new TrainerNotFoundException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  void by_ShouldReturnExceptionWithCorrectMessage_ById() {
    UUID id = UUID.randomUUID();

    TrainerNotFoundException exception = TrainerNotFoundException.by(id);

    assertEquals("Trainer not found by trainer ID: " + id, exception.getMessage());
  }
}
