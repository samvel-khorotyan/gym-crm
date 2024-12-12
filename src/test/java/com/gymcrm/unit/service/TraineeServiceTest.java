package com.gymcrm.unit.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.UpdateTraineeCommand;
import com.gymcrm.domain.Trainee;
import com.gymcrm.prot.LoadTraineePort;
import com.gymcrm.prot.UpdateTraineePort;
import com.gymcrm.service.TraineeService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
  @Mock private UpdateTraineePort updateTraineePort;

  @Mock private LoadTraineePort loadTraineePort;

  @InjectMocks private TraineeService traineeService;

  private Trainee trainee;
  private UpdateTraineeCommand updateTraineeCommand;

  @BeforeEach
  void setUp() {
    trainee = new Trainee();
    trainee.setId(UUID.randomUUID());
    trainee.setAddress("123 Street");
    trainee.setDateOfBirth(LocalDate.of(2000, 1, 1));

    updateTraineeCommand = new UpdateTraineeCommand();
    updateTraineeCommand.setTraineeId(trainee.getId());
    updateTraineeCommand.setAddress("456 Avenue");
    updateTraineeCommand.setDateOfBirth(LocalDate.of(1995, 5, 15));

    traineeService.setLoadTraineePort(loadTraineePort);
  }

  @Test
  void createTrainee_shouldSaveTrainee() {
    doNothing().when(updateTraineePort).saveOrUpdate(trainee);

    traineeService.createTrainee(trainee);

    verify(updateTraineePort).saveOrUpdate(trainee);
  }

  @Test
  void getAllTrainees_shouldReturnListOfTrainees() {
    List<Trainee> trainees = List.of(trainee);
    when(loadTraineePort.fetchAll()).thenReturn(trainees);

    List<Trainee> result = traineeService.getAllTrainees();

    assertEquals(trainees, result);
    verify(loadTraineePort).fetchAll();
  }

  @Test
  void getTraineeById_shouldReturnTrainee() {
    UUID traineeId = trainee.getId();
    when(loadTraineePort.fetchById(traineeId)).thenReturn(trainee);

    Trainee result = traineeService.getTraineeById(traineeId);

    assertEquals(trainee, result);
    verify(loadTraineePort).fetchById(traineeId);
  }

  @Test
  void getTraineeById_shouldThrowExceptionWhenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> traineeService.getTraineeById(null));
  }

  @Test
  void getTraineeById_shouldThrowExceptionWhenTraineeNotFound() {
    UUID traineeId = UUID.randomUUID();
    when(loadTraineePort.fetchById(traineeId)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> traineeService.getTraineeById(traineeId));
  }

  @Test
  void updateTrainee_shouldUpdateTrainee() {
    doNothing().when(updateTraineePort).saveOrUpdate(trainee);
    when(loadTraineePort.fetchById(updateTraineeCommand.getTraineeId())).thenReturn(trainee);

    traineeService.updateTrainee(updateTraineeCommand);

    verify(updateTraineePort).saveOrUpdate(trainee);
    assertEquals(updateTraineeCommand.getAddress(), trainee.getAddress());
    assertEquals(updateTraineeCommand.getDateOfBirth(), trainee.getDateOfBirth());
  }

  @Test
  void updateTrainee_shouldThrowExceptionWhenIdIsNull() {
    updateTraineeCommand.setTraineeId(null);

    assertThrows(
        IllegalArgumentException.class, () -> traineeService.updateTrainee(updateTraineeCommand));
  }

  @Test
  void updateTrainee_shouldThrowExceptionWhenTraineeNotFound() {
    UUID traineeId = UUID.randomUUID();
    updateTraineeCommand.setTraineeId(traineeId);
    when(loadTraineePort.fetchById(traineeId)).thenReturn(null);

    assertThrows(
        IllegalArgumentException.class, () -> traineeService.updateTrainee(updateTraineeCommand));
  }

  @Test
  void removeTrainee_shouldRemoveTrainee() {
    UUID traineeId = trainee.getId();
    when(loadTraineePort.fetchById(traineeId)).thenReturn(trainee);
    doNothing().when(updateTraineePort).removeById(trainee.getId());

    traineeService.removeTrainee(traineeId);

    verify(updateTraineePort).removeById(traineeId);
  }

  @Test
  void removeTrainee_shouldThrowExceptionWhenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> traineeService.removeTrainee(null));
  }

  @Test
  void removeTrainee_shouldThrowExceptionWhenTraineeNotFound() {
    UUID traineeId = UUID.randomUUID();
    when(loadTraineePort.fetchById(traineeId)).thenReturn(null);

    assertThrows(IllegalArgumentException.class, () -> traineeService.removeTrainee(traineeId));
  }
}
