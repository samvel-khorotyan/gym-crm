package com.gymcrm.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.CreateTrainerCommand;
import com.gymcrm.command.UpdateTrainerCommand;
import com.gymcrm.domain.Trainer;
import com.gymcrm.factory.TrainerFactory;
import com.gymcrm.prot.LoadTrainerPort;
import com.gymcrm.prot.UpdateTrainerPort;
import com.gymcrm.service.TrainerService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
  @Mock private UpdateTrainerPort updateTrainerPort;

  @Mock private LoadTrainerPort loadTrainerPort;

  @Mock private TrainerFactory trainerFactory;

  @InjectMocks private TrainerService trainerService;

  private CreateTrainerCommand createTrainerCommand;
  private UpdateTrainerCommand updateTrainerCommand;
  private Trainer trainer;

  @BeforeEach
  void setUp() {
    createTrainerCommand = new CreateTrainerCommand();
    createTrainerCommand.setSpecialization("Fitness");
    createTrainerCommand.setUserFirstName("John");
    createTrainerCommand.setUserLastName("Doe");

    updateTrainerCommand = new UpdateTrainerCommand();
    updateTrainerCommand.setTrainerId(UUID.randomUUID());
    updateTrainerCommand.setSpecialization("Yoga");

    trainer = new Trainer();
    trainer.setId(UUID.randomUUID());
    trainer.setSpecialization("Fitness");
    trainerService.setLoadTrainerPort(loadTrainerPort);
  }

  @Test
  void createTrainer_shouldSaveTrainer() {
    doNothing().when(updateTrainerPort).saveOrUpdate(trainer);
    when(trainerFactory.createFrom(createTrainerCommand)).thenReturn(trainer);

    trainerService.createTrainer(createTrainerCommand);

    verify(trainerFactory).createFrom(createTrainerCommand);
    verify(updateTrainerPort).saveOrUpdate(trainer);
  }

  @Test
  void createTrainer_shouldThrowExceptionWhenSpecializationIsNull() {
    createTrainerCommand.setSpecialization(null);

    assertThrows(
        IllegalArgumentException.class, () -> trainerService.createTrainer(createTrainerCommand));
  }

  @Test
  void getAllTrainers_shouldReturnListOfTrainers() {
    List<Trainer> trainers = List.of(trainer);
    when(loadTrainerPort.fetchAll()).thenReturn(trainers);

    List<Trainer> result = trainerService.getAllTrainers();

    assertEquals(trainers, result);
    verify(loadTrainerPort).fetchAll();
  }

  @Test
  void getTrainerById_shouldReturnTrainer() {
    UUID trainerId = trainer.getId();
    when(loadTrainerPort.fetchById(trainerId)).thenReturn(trainer);

    Trainer result = trainerService.getTrainerById(trainerId);

    assertEquals(trainer, result);
    verify(loadTrainerPort).fetchById(trainerId);
  }

  @Test
  void getTrainerById_shouldThrowExceptionWhenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> trainerService.getTrainerById(null));
  }

  @Test
  void getTrainerById_shouldThrowExceptionWhenTrainerNotFound() {
    UUID trainerId = UUID.randomUUID();
    when(loadTrainerPort.fetchById(trainerId)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> trainerService.getTrainerById(trainerId));
  }

  @Test
  void updateTrainer_shouldUpdateTrainer() {
    doNothing().when(updateTrainerPort).saveOrUpdate(trainer);
    when(loadTrainerPort.fetchById(updateTrainerCommand.getTrainerId())).thenReturn(trainer);

    trainerService.updateTrainer(updateTrainerCommand);

    verify(updateTrainerPort).saveOrUpdate(trainer);
    assertEquals(updateTrainerCommand.getSpecialization(), trainer.getSpecialization());
  }

  @Test
  void updateTrainer_shouldThrowExceptionWhenIdIsNull() {
    updateTrainerCommand.setTrainerId(null);

    assertThrows(
        IllegalArgumentException.class, () -> trainerService.updateTrainer(updateTrainerCommand));
  }

  @Test
  void updateTrainer_shouldThrowExceptionWhenTrainerNotFound() {
    UUID trainerId = UUID.randomUUID();
    updateTrainerCommand.setTrainerId(trainerId);
    when(loadTrainerPort.fetchById(trainerId)).thenReturn(null);

    assertThrows(
        IllegalArgumentException.class, () -> trainerService.updateTrainer(updateTrainerCommand));
  }
}
