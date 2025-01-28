package com.gymcrm.unit.trainer.application.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainer.application.factory.TrainerFactory;
import com.gymcrm.trainer.application.port.input.CreateTrainerCommand;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.domain.User;
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
    User user = new User(userId, "John", "Doe", "john.doe", "password123", true, null);

    CreateTrainerCommand command = new CreateTrainerCommand(specialization, user);

    when(uuidGeneratorInterface.newUUID()).thenReturn(generatedUUID);

    Trainer trainer = trainerFactory.createFrom(command);

    assertNotNull(trainer, "Trainer object should not be null");
    assertEquals(generatedUUID, trainer.getId(), "Trainer ID should match generated UUID");
    assertEquals(
        specialization, trainer.getSpecialization(), "Specialization should match command");
    assertEquals(user, trainer.getUser(), "User should match command");

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
