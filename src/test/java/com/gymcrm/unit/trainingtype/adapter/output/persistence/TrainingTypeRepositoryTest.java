package com.gymcrm.unit.trainingtype.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainingtype.adapter.output.persistence.TrainingTypePersistenceRepository;
import com.gymcrm.trainingtype.adapter.output.persistence.TrainingTypeRepository;
import com.gymcrm.trainingtype.application.exception.TrainingTypeNotFoundException;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRepositoryTest {
  @Mock private TrainingTypePersistenceRepository repository;

  @InjectMocks private TrainingTypeRepository trainingTypeRepository;

  private TrainingType trainingType;
  private UUID trainingTypeId;

  @BeforeEach
  void setUp() {
    trainingTypeId = UUID.randomUUID();
    trainingType = new TrainingType(trainingTypeId, "Wrestling");
  }

  @Test
  void save_ShouldSaveTrainingType() {
    when(repository.save(trainingType)).thenReturn(trainingType);

    TrainingType savedTrainingType = trainingTypeRepository.save(trainingType);

    assertNotNull(savedTrainingType);
    assertEquals(trainingType, savedTrainingType);
    verify(repository, times(1)).save(trainingType);
  }

  @Test
  void findById_ShouldReturnTrainingType_WhenTrainingTypeExists() {
    when(repository.findById(trainingTypeId)).thenReturn(Optional.of(trainingType));

    TrainingType foundTrainingType = trainingTypeRepository.findById(trainingTypeId);

    assertNotNull(foundTrainingType);
    assertEquals(trainingType, foundTrainingType);
    verify(repository, times(1)).findById(trainingTypeId);
  }

  @Test
  void findById_ShouldThrowTrainingTypeNotFoundException_WhenTrainingTypeDoesNotExist() {
    when(repository.findById(trainingTypeId)).thenReturn(Optional.empty());

    assertThrows(
        TrainingTypeNotFoundException.class, () -> trainingTypeRepository.findById(trainingTypeId));
    verify(repository, times(1)).findById(trainingTypeId);
  }

  @Test
  void findByTrainingTypeName_ShouldReturnTrainingType_WhenTrainingTypeExists() {
    String trainingTypeName = "Wrestling";
    when(repository.findByTrainingTypeName(trainingTypeName)).thenReturn(Optional.of(trainingType));

    TrainingType foundTrainingType =
        trainingTypeRepository.findByTrainingTypeName(trainingTypeName);

    assertNotNull(foundTrainingType);
    assertEquals(trainingType, foundTrainingType);
    verify(repository, times(1)).findByTrainingTypeName(trainingTypeName);
  }

  @Test
  void
      findByTrainingTypeName_ShouldThrowTrainingTypeNotFoundException_WhenTrainingTypeDoesNotExist() {
    String trainingTypeName = "NonExistent";
    when(repository.findByTrainingTypeName(trainingTypeName)).thenReturn(Optional.empty());

    assertThrows(
        TrainingTypeNotFoundException.class,
        () -> trainingTypeRepository.findByTrainingTypeName(trainingTypeName));
    verify(repository, times(1)).findByTrainingTypeName(trainingTypeName);
  }
}
