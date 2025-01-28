package com.gymcrm.unit.trainee.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.adapter.output.persistence.TraineePersistenceRepository;
import com.gymcrm.trainee.adapter.output.persistence.TraineeRepository;
import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.time.LocalDate;
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
class TraineeRepositoryTest {
  @Mock private TraineePersistenceRepository repository;

  @InjectMocks private TraineeRepository traineeRepository;

  private UUID traineeId;
  private Trainee trainee;

  @BeforeEach
  void setUp() {
    traineeId = UUID.randomUUID();
    User user =
        new User(
            UUID.randomUUID(), "John", "Doe", "john.doe", "password123", true, UserType.TRAINEE);
    trainee = new Trainee(traineeId, LocalDate.of(2000, 1, 1), "123 Main St", user);
  }

  @Test
  void findById_ShouldReturnTrainee_WhenTraineeExists() {
    when(repository.findById(traineeId)).thenReturn(Optional.of(trainee));

    Trainee foundTrainee = traineeRepository.findById(traineeId);

    assertEquals(trainee, foundTrainee);
    verify(repository, times(1)).findById(traineeId);
  }

  @Test
  void findById_ShouldThrowException_WhenTraineeDoesNotExist() {
    when(repository.findById(traineeId)).thenReturn(Optional.empty());

    assertThrows(TraineeNotFoundException.class, () -> traineeRepository.findById(traineeId));
    verify(repository, times(1)).findById(traineeId);
  }

  @Test
  void findAll_ShouldReturnListOfTrainees() {
    Trainee trainee2 =
        new Trainee(UUID.randomUUID(), LocalDate.of(1995, 5, 15), "456 Main St", new User());
    when(repository.findAll()).thenReturn(List.of(trainee, trainee2));

    List<Trainee> trainees = traineeRepository.findAll();

    assertEquals(2, trainees.size());
    assertTrue(trainees.contains(trainee));
    assertTrue(trainees.contains(trainee2));
    verify(repository, times(1)).findAll();
  }

  @Test
  void save_ShouldSaveTrainee() {
    when(repository.save(trainee)).thenReturn(trainee); // Նշում ենք վերադարձվող արժեքը։

    traineeRepository.save(trainee);

    verify(repository, times(1)).save(trainee);
  }

  @Test
  void deleteById_ShouldDeleteTrainee_WhenTraineeExists() {
    doNothing().when(repository).deleteById(traineeId);

    traineeRepository.deleteById(traineeId);

    verify(repository, times(1)).deleteById(traineeId);
  }

  @Test
  void deleteByUsername_ShouldDeleteTraineeByUsername() {
    String username = "john.doe";
    doNothing().when(repository).deleteByUsername(username);

    traineeRepository.deleteByUsername(username);

    verify(repository, times(1)).deleteByUsername(username);
  }
}
