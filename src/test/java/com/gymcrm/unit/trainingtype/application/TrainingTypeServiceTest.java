package com.gymcrm.unit.trainingtype.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainingtype.application.TrainingTypeService;
import com.gymcrm.trainingtype.application.exception.TrainingTypeNotFoundException;
import com.gymcrm.trainingtype.application.factory.TrainingTypeFactory;
import com.gymcrm.trainingtype.application.port.input.CreateTrainingTypeCommand;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.application.port.output.UpdateTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {
  @Mock private TrainingTypeFactory trainingTypeFactory;

  @Mock private UpdateTrainingTypePort updateTrainingTypePort;

  @Mock private LoadTrainingTypePort loadTrainingTypePort;

  @InjectMocks private TrainingTypeService trainingTypeService;

  private CreateTrainingTypeCommand validCommand;

  @BeforeEach
  void setUp() {
    validCommand = new CreateTrainingTypeCommand("Wrestling");
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenTrainingTypeNameIsNull() {
    validCommand.setTrainingTypeName(null);

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> trainingTypeService.create(validCommand));

    assertEquals("Training type name cannot be null or empty", exception.getMessage());
  }

  @Test
  void create_ShouldThrowIllegalArgumentException_WhenTrainingTypeNameIsBlank() {
    validCommand.setTrainingTypeName("   ");

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> trainingTypeService.create(validCommand));

    assertEquals("Training type name cannot be null or empty", exception.getMessage());
  }

  @Test
  void create_ShouldReturnExistingTrainingType_WhenFoundByLoadPort() {
    TrainingType existingTrainingType = new TrainingType();
    when(loadTrainingTypePort.findByTrainingTypeName("Wrestling")).thenReturn(existingTrainingType);

    // Act
    TrainingType result = trainingTypeService.create(validCommand);

    // Assert
    assertNotNull(result);
    assertEquals(existingTrainingType, result);
    verify(loadTrainingTypePort, times(1)).findByTrainingTypeName("Wrestling");
    verifyNoInteractions(trainingTypeFactory, updateTrainingTypePort);
  }

  @Test
  void create_ShouldSaveNewTrainingType_WhenNotFound() {
    TrainingType newTrainingType = new TrainingType();
    when(loadTrainingTypePort.findByTrainingTypeName("Wrestling"))
        .thenThrow(TrainingTypeNotFoundException.by("Wrestling"));
    when(trainingTypeFactory.createFrom(validCommand)).thenReturn(newTrainingType);
    when(updateTrainingTypePort.save(newTrainingType)).thenReturn(newTrainingType);

    TrainingType result = trainingTypeService.create(validCommand);

    assertNotNull(result);
    assertEquals(newTrainingType, result);
    verify(loadTrainingTypePort, times(1)).findByTrainingTypeName("Wrestling");
    verify(trainingTypeFactory, times(1)).createFrom(validCommand);
    verify(updateTrainingTypePort, times(1)).save(newTrainingType);
  }
}
