package com.gymcrm.unit.trainer.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainer.adapter.output.persistence.TrainerPersistenceRepository;
import com.gymcrm.trainer.adapter.output.persistence.TrainerRepository;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryTest {
  @Mock private TrainerPersistenceRepository repository;

  @InjectMocks private TrainerRepository trainerRepository;

  private Trainer trainer;
  private UUID trainerId;
  private User user;

  @BeforeEach
  void setUp() {
    trainerId = UUID.randomUUID();
    user = new User(trainerId, "John", "Doe", "johndoe", "password", true, UserType.TRAINEE);
    trainer = new Trainer(trainerId, "Specialization", user);
  }

  @Test
  void save_ShouldSaveTrainer() {
    when(repository.save(trainer)).thenReturn(trainer);

    trainerRepository.save(trainer);

    verify(repository, times(1)).save(trainer);
  }

  @Test
  void findById_ShouldReturnTrainer_WhenTrainerExists() {
    when(repository.findById(trainerId)).thenReturn(Optional.of(trainer));

    Trainer result = trainerRepository.findById(trainerId);

    assertNotNull(result);
    assertEquals(trainer, result);
    verify(repository, times(1)).findById(trainerId);
  }

  @Test
  void findById_ShouldThrowTrainerNotFoundException_WhenTrainerDoesNotExist() {
    when(repository.findById(trainerId)).thenReturn(Optional.empty());

    assertThrows(TrainerNotFoundException.class, () -> trainerRepository.findById(trainerId));
    verify(repository, times(1)).findById(trainerId);
  }

  @Test
  void findAll_ShouldReturnAllTrainers() {
    Trainer anotherTrainer = new Trainer(UUID.randomUUID(), "Specialization2", user);
    when(repository.findAll()).thenReturn(Arrays.asList(trainer, anotherTrainer));

    List<Trainer> result = trainerRepository.findAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(repository, times(1)).findAll();
  }

  @Test
  void findTrainersNotAssignedToTrainee_ShouldReturnCorrectTrainers() {
    String trainerName = "John";
    when(repository.findTrainersNotAssignedToTrainee(trainerName)).thenReturn(List.of(trainer));

    List<Trainer> result = trainerRepository.findTrainersNotAssignedToTrainee(trainerName);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(trainer, result.get(0));
    verify(repository, times(1)).findTrainersNotAssignedToTrainee(trainerName);
  }
}
