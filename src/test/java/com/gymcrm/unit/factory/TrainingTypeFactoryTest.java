package com.gymcrm.unit.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainingtype.application.factory.TrainingTypeFactory;
import com.gymcrm.trainingtype.application.port.input.CreateTrainingTypeCommand;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingTypeFactoryTest {
  @Mock private UUIDGeneratorInterface uuidGeneratorInterface;

  @InjectMocks private TrainingTypeFactory trainingTypeFactory;

  @Test
  void testCreateFrom_createsTrainingTypeWithCorrectFields() {
    UUID generatedUUID = UUID.randomUUID();
    String trainingTypeName = "Weightlifting";

    CreateTrainingTypeCommand command = new CreateTrainingTypeCommand(trainingTypeName);

    when(uuidGeneratorInterface.newUUID()).thenReturn(generatedUUID);

    TrainingType trainingType = trainingTypeFactory.createFrom(command);

    assertNotNull(trainingType, "TrainingType object should not be null");
    assertEquals(
        generatedUUID, trainingType.getId(), "TrainingType ID should match generated UUID");
    assertEquals(
        trainingTypeName,
        trainingType.getTrainingTypeName(),
        "TrainingType name should match command");

    verify(uuidGeneratorInterface, times(1)).newUUID();
  }

  @Test
  void testCreateFrom_handlesNullCommand() {
    assertThrows(
        NullPointerException.class,
        () -> trainingTypeFactory.createFrom(null),
        "Should throw NullPointerException when command is null");
  }
}
