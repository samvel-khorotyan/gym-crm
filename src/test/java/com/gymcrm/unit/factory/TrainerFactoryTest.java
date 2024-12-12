package com.gymcrm.unit.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.CreateTrainerCommand;
import com.gymcrm.domain.Trainer;
import com.gymcrm.factory.TrainerFactory;
import com.gymcrm.util.UUIDGeneratorInterface;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerFactoryTest {
  @Mock private UUIDGeneratorInterface uuidGeneratorInterface;

  @InjectMocks private TrainerFactory trainerFactory;

  @Test
  void testCreateFrom_createsTrainerWithCorrectFields() {
    UUID generatedUUID = UUID.randomUUID();
    String specialization = "Fitness Coach";
    UUID userId = UUID.randomUUID();
    CreateTrainerCommand command = new CreateTrainerCommand();
    command.setSpecialization(specialization);
    command.setUserId(userId);

    when(uuidGeneratorInterface.newUUID()).thenReturn(generatedUUID);

    Trainer trainer = trainerFactory.createFrom(command);

    assertNotNull(trainer, "Trainer object should not be null");
    assertEquals(generatedUUID, trainer.getId(), "Trainer ID should match generated UUID");
    assertEquals(
        specialization, trainer.getSpecialization(), "Specialization should match command");
    assertEquals(userId, trainer.getUserId(), "User ID should match command");

    verify(uuidGeneratorInterface, times(1)).newUUID();
  }

  @Test
  void testCreateFrom_handlesNullCommand() {
    assertThrows(
        NullPointerException.class,
        () -> trainerFactory.createFrom(null),
        "Should throw NullPointerException when command is null");
  }
}
