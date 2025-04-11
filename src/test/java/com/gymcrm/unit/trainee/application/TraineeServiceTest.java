package com.gymcrm.unit.trainee.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.adapter.input.web.mapper.TraineeUpdateMapper;
import com.gymcrm.trainee.application.TraineeService;
import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.application.factory.TraineeFactory;
import com.gymcrm.trainee.application.port.input.ActivateDeactivateTraineeCommand;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import com.gymcrm.trainee.application.port.input.UpdateTraineeCommand;
import com.gymcrm.trainee.application.port.input.UpdateTraineeTrainersCommand;
import com.gymcrm.trainee.application.port.output.LoadTraineePort;
import com.gymcrm.trainee.application.port.output.UpdateTraineePort;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.output.LoadTrainingPort;
import com.gymcrm.training.application.port.output.UpdateTrainingPort;
import com.gymcrm.training.domain.Training;
import com.gymcrm.user.adapter.input.web.mapper.UserUpdateMapper;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.UpdateUserCommand;
import com.gymcrm.user.application.port.input.UserCreationUseCase;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {
	@Mock
	private UpdateTraineePort updateTraineePort;

	@Mock
	private UserCreationUseCase userCreationUseCase;

	@Mock
	private TraineeFactory traineeFactory;

	@Mock
	private LoadTraineePort loadTraineePort;

	@Mock
	private UserUpdateMapper userUpdateMapper;

	@Mock
	private TraineeUpdateMapper traineeUpdateMapper;

	@Mock
	private UpdateUserPort updateUserPort;

	@Mock
	private LoadTrainerPort loadTrainerPort;

	@Mock
	private LoadTrainingPort loadTrainingPort;

	@Mock
	private UpdateTrainingPort updateTrainingPort;

	@Mock
	private TrainingFactory trainingFactory;

	@InjectMocks
	private TraineeService traineeService;

	private User mockUser;

	@BeforeEach
	void setUp() {
		mockUser = new User(UUID.randomUUID(), "John", "Doe", "john.doe", "password123", true, UserType.TRAINEE);

		traineeService.setLoadTraineePort(loadTraineePort);
	}

	@Test
	void create_ShouldReturnTrainee_WhenCommandIsValid() {
		MDC.put("transactionId", "12345");
		CreateTraineeCommand command = new CreateTraineeCommand("John", "Doe", null, "123 Main St", null);
		User mockUser = new User();
		mockUser.setUsername("john.doe");
		Trainee mockTrainee = new Trainee();
		UUID traineeId = UUID.randomUUID();
		mockTrainee.setId(traineeId);

		when(userCreationUseCase.create(any(CreateUserCommand.class))).thenReturn(mockUser);
		when(traineeFactory.createFrom(command)).thenReturn(mockTrainee);
		when(updateTraineePort.save(mockTrainee)).thenReturn(mockTrainee);

		Trainee result = traineeService.create(command);

		assertNotNull(result);
		assertEquals(traineeId, result.getId());
		verify(userCreationUseCase, times(1)).create(any(CreateUserCommand.class));
		verify(traineeFactory, times(1)).createFrom(command);
		verify(updateTraineePort, times(1)).save(mockTrainee);
	}

	@Test
	void create_ShouldThrowException_WhenUserCreationFails() {
		MDC.put("transactionId", "12345");
		CreateTraineeCommand command = new CreateTraineeCommand("John", "Doe", null, "123 Main St", null);

		when(userCreationUseCase.create(any(CreateUserCommand.class)))
		        .thenThrow(new RuntimeException("User creation failed"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> traineeService.create(command));
		assertEquals("User creation failed", exception.getMessage());
		verify(userCreationUseCase, times(1)).create(any(CreateUserCommand.class));
		verify(traineeFactory, never()).createFrom(command);
		verify(updateTraineePort, never()).save(any());
	}

	@Test
	void create_ShouldThrowException_WhenTraineeSaveFails() {
		MDC.put("transactionId", "12345");
		CreateTraineeCommand command = new CreateTraineeCommand("John", "Doe", null, "123 Main St", null);
		User mockUser = new User();
		mockUser.setUsername("john.doe");
		Trainee mockTrainee = new Trainee();

		when(userCreationUseCase.create(any(CreateUserCommand.class))).thenReturn(mockUser);
		when(traineeFactory.createFrom(command)).thenReturn(mockTrainee);
		when(updateTraineePort.save(mockTrainee)).thenThrow(new RuntimeException("Trainee save failed"));

		RuntimeException exception = assertThrows(RuntimeException.class, () -> traineeService.create(command));
		assertEquals("Trainee save failed", exception.getMessage());
		verify(userCreationUseCase, times(1)).create(any(CreateUserCommand.class));
		verify(traineeFactory, times(1)).createFrom(command);
		verify(updateTraineePort, times(1)).save(mockTrainee);
	}

	@Test
	void loadAll_ShouldReturnListOfTrainees_WhenTraineesExist() {
		List<Trainee> mockTrainees = List.of(
		        new Trainee(UUID.randomUUID(), LocalDate.now(), "Address 1",
		                new User(UUID.randomUUID(), "John", "Doe", "john.doe", "password123", true, UserType.TRAINEE)),
		        new Trainee(UUID.randomUUID(), LocalDate.now(), "Address 2",
		                new User(UUID.randomUUID(), "John", "Doe", "john.doe", "password123", true, UserType.TRAINEE)));

		when(loadTraineePort.findAll()).thenReturn(mockTrainees);

		List<Trainee> result = traineeService.loadAll();

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(loadTraineePort, times(1)).findAll();
	}

	@Test
	void loadAll_ShouldThrowRuntimeException_WhenFetchingFails() {
		RuntimeException exception = new RuntimeException("Database error");
		when(loadTraineePort.findAll()).thenThrow(exception);

		RuntimeException thrown = assertThrows(RuntimeException.class, () -> traineeService.loadAll());
		assertEquals("Failed to fetch all trainees", thrown.getMessage());
		assertEquals(exception, thrown.getCause());
		verify(loadTraineePort, times(1)).findAll();
	}

	@Test
  void loadAll_ShouldReturnEmptyList_WhenNoTraineesExist() {
    when(loadTraineePort.findAll()).thenReturn(List.of());

    List<Trainee> result = traineeService.loadAll();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(loadTraineePort, times(1)).findAll();
  }

	@Test
	void loadByUsername_ShouldReturnTrainee_WhenTraineeExists() {
		String username = "john.doe";
		Trainee mockTrainee = new Trainee();
		mockTrainee.setUser(new User(null, "John", "Doe", username, null, true, null));

		when(loadTraineePort.findByUsernameWithTrainers(username)).thenReturn(mockTrainee);

		Trainee result = traineeService.loadByUsername(username);

		assertNotNull(result);
		assertEquals(username, result.getUser().getUsername());
		verify(loadTraineePort, times(1)).findByUsernameWithTrainers(username);
	}

	@Test
	void loadByUsername_ShouldThrowTraineeNotFoundException_WhenTraineeDoesNotExist() {
		String username = "unknown.user";

		when(loadTraineePort.findByUsernameWithTrainers(username))
		        .thenThrow(new TraineeNotFoundException("Trainee not found"));

		TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class,
		        () -> traineeService.loadByUsername(username));
		assertEquals("Trainee not found", exception.getMessage());
		verify(loadTraineePort, times(1)).findByUsernameWithTrainers(username);
	}

	@Test
	void loadByUsername_ShouldLogErrorAndThrowException_WhenUnexpectedErrorOccurs() {
		String username = "john.doe";
		RuntimeException unexpectedException = new RuntimeException("Unexpected error");

		when(loadTraineePort.findByUsernameWithTrainers(username)).thenThrow(unexpectedException);

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> traineeService.loadByUsername(username));
		assertEquals("Unexpected error", exception.getMessage());
		verify(loadTraineePort, times(1)).findByUsernameWithTrainers(username);
	}

	@Test
	void update_ShouldUpdateTraineeSuccessfully_WhenValidCommandIsGiven() {
		UUID traineeId = UUID.randomUUID();
		Trainee existingTrainee = new Trainee(traineeId, LocalDate.now(), "Old Address", null);
		UpdateTraineeCommand command = new UpdateTraineeCommand(traineeId, "New First Name", "New Last Name",
		        LocalDate.now(), "New Address", true);

		when(loadTraineePort.findByIdWithTrainers(traineeId)).thenReturn(existingTrainee);
		when(updateTraineePort.save(existingTrainee)).thenReturn(existingTrainee);

		Trainee result = traineeService.update(command);

		assertNotNull(result);
		verify(loadTraineePort, times(1)).findByIdWithTrainers(traineeId);
		verify(userUpdateMapper, times(1)).updateUserFromCommand(any(UpdateUserCommand.class),
		        eq(existingTrainee.getUser()));
		verify(traineeUpdateMapper, times(1)).updateTraineeFromCommand(eq(command), eq(existingTrainee));
		verify(updateTraineePort, times(1)).save(existingTrainee);
	}

	@Test
	void update_ShouldThrowException_WhenTraineeNotFound() {
		UUID traineeId = UUID.randomUUID();
		UpdateTraineeCommand command = new UpdateTraineeCommand(traineeId, "New First Name", "New Last Name",
		        LocalDate.now(), "New Address", true);

		when(loadTraineePort.findByIdWithTrainers(traineeId)).thenThrow(new RuntimeException("Trainee not found"));

		Exception exception = assertThrows(RuntimeException.class, () -> traineeService.update(command));
		assertEquals("Trainee not found", exception.getMessage());
		verify(loadTraineePort, times(1)).findByIdWithTrainers(traineeId);
		verifyNoInteractions(userUpdateMapper, traineeUpdateMapper, updateTraineePort);
	}

	@Test
	void update_ShouldThrowException_WhenUnexpectedErrorOccurs() {
		UUID traineeId = UUID.randomUUID();
		Trainee existingTrainee = new Trainee(traineeId, LocalDate.now(), "Old Address", null);
		UpdateTraineeCommand command = new UpdateTraineeCommand(traineeId, "New First Name", "New Last Name",
		        LocalDate.now(), "New Address", true);

		when(loadTraineePort.findByIdWithTrainers(traineeId)).thenReturn(existingTrainee);
		doThrow(new RuntimeException("Unexpected error")).when(updateTraineePort).save(existingTrainee);

		Exception exception = assertThrows(RuntimeException.class, () -> traineeService.update(command));
		assertEquals("Unexpected error", exception.getMessage());
		verify(loadTraineePort, times(1)).findByIdWithTrainers(traineeId);
		verify(updateTraineePort, times(1)).save(existingTrainee);
	}

	@Test
	void activateDeactivate_ShouldActivateTrainee_WhenUsernameIsValid() {
		String username = "john.doe";
		User mockUser = new User(UUID.randomUUID(), "John", "Doe", username, "password", true, UserType.TRAINEE);
		Trainee mockTrainee = new Trainee(UUID.randomUUID(), LocalDate.now(), "Address", mockUser);
		mockUser.setTrainee(mockTrainee);

		ActivateDeactivateTraineeCommand command = new ActivateDeactivateTraineeCommand(username, true);

		when(loadTraineePort.findByUsername(username)).thenReturn(mockTrainee);

		traineeService.activateDeactivate(command);

		verify(loadTraineePort, times(1)).findByUsername(username);
		verify(updateUserPort, times(1)).save(mockUser);
	}

	@Test
	void activateDeactivate_ShouldDeactivateTrainee_WhenUsernameIsValid() {
		String username = "john.doe";
		mockUser.setTrainee(new Trainee(UUID.randomUUID(), LocalDate.now(), "Address", mockUser));
		ActivateDeactivateTraineeCommand command = new ActivateDeactivateTraineeCommand(username, false);

		when(loadTraineePort.findByUsername(username)).thenReturn(mockUser.getTrainee());

		traineeService.activateDeactivate(command);

		verify(loadTraineePort, times(1)).findByUsername(username);
		verify(updateUserPort, times(1)).save(mockUser);
	}

	@Test
	void activateDeactivate_ShouldThrowTraineeNotFoundException_WhenTraineeDoesNotExist() {
		String username = "nonexistent.username";
		ActivateDeactivateTraineeCommand command = new ActivateDeactivateTraineeCommand(username, true);

		when(loadTraineePort.findByUsername(username)).thenThrow(new TraineeNotFoundException("Trainee not found"));

		assertThrows(TraineeNotFoundException.class, () -> traineeService.activateDeactivate(command));

		verify(loadTraineePort, times(1)).findByUsername(username);
		verifyNoInteractions(updateUserPort);
	}

	@Test
	void activateDeactivate_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
		String username = "john.doe";
		ActivateDeactivateTraineeCommand command = new ActivateDeactivateTraineeCommand(username, true);

		when(loadTraineePort.findByUsername(username)).thenThrow(new RuntimeException("Database error"));

		assertThrows(RuntimeException.class, () -> traineeService.activateDeactivate(command));

		verify(loadTraineePort, times(1)).findByUsername(username);
		verifyNoInteractions(updateUserPort);
	}

	@Test
	void updateTraineeTrainers_ShouldUpdateTrainee_WhenAllDataIsValid() {
		String traineeUsername = "john.doe";
		List<String> trainerUsernames = List.of("trainer1", "trainer2");

		User traineeUser = new User(UUID.randomUUID(), "John", "Doe", traineeUsername, "password", true,
		        UserType.TRAINEE);
		User trainerUser1 = new User(UUID.randomUUID(), "Trainer", "One", "trainer1", "password", true,
		        UserType.TRAINER);
		User trainerUser2 = new User(UUID.randomUUID(), "Trainer", "Two", "trainer2", "password", true,
		        UserType.TRAINER);

		Trainer trainer1 = new Trainer(UUID.randomUUID(), "Fitness", trainerUser1);
		Trainer trainer2 = new Trainer(UUID.randomUUID(), "Yoga", trainerUser2);

		Trainee trainee = new Trainee(UUID.randomUUID(), LocalDate.of(1995, 1, 1), "123 Street", traineeUser);

		Training training1 = new Training(UUID.randomUUID(), "Morning Training", trainee, trainer1, null,
		        LocalDate.now(), 60);
		Training training2 = new Training(UUID.randomUUID(), "Evening Training", trainee, trainer2, null,
		        LocalDate.now().plusDays(1), 90);

		List<Trainer> trainers = List.of(trainer1, trainer2);
		List<Training> trainings = List.of(training1, training2);

		when(loadTraineePort.findByUsername(traineeUsername)).thenReturn(trainee);
		when(loadTrainerPort.findAllByUsernames(trainerUsernames)).thenReturn(trainers);
		when(loadTrainingPort.findAllByTrainerUsernames(trainerUsernames)).thenReturn(trainings);
		when(updateTraineePort.save(trainee)).thenReturn(trainee); // Mocking save method

		UpdateTraineeTrainersCommand command = new UpdateTraineeTrainersCommand(traineeUsername, trainerUsernames);
		Trainee updatedTrainee = traineeService.updateTraineeTrainers(command);

		assertNotNull(updatedTrainee, "Updated trainee should not be null");
		assertEquals(trainers, updatedTrainee.getTrainers());
		assertSame(trainee, updatedTrainee, "The returned trainee should be the same as the saved one.");
		verify(updateTrainingPort, times(1)).deleteByTraineeId(trainee.getId());
		verify(updateTrainingPort, times(1)).saveAll(anyList());
		verify(updateTraineePort, times(1)).save(trainee);
	}

	@Test
	void updateTraineeTrainers_ShouldThrowException_WhenTraineeNotFound() {
		String traineeUsername = "invalid.trainee";
		List<String> trainerUsernames = List.of("trainer1", "trainer2");

		when(loadTraineePort.findByUsername(traineeUsername))
		        .thenThrow(new TraineeNotFoundException("Trainee not found"));

		UpdateTraineeTrainersCommand command = new UpdateTraineeTrainersCommand(traineeUsername, trainerUsernames);

		assertThrows(TraineeNotFoundException.class, () -> traineeService.updateTraineeTrainers(command));

		verify(updateTrainingPort, never()).deleteByTraineeId(any());
		verify(updateTraineePort, never()).save(any());
	}

	@Test
	void updateTraineeTrainers_ShouldThrowException_WhenTrainersNotFound() {
		String traineeUsername = "john.doe";
		List<String> trainerUsernames = List.of("invalid.trainer1", "invalid.trainer2");

		Trainee trainee = mock(Trainee.class);
		when(loadTraineePort.findByUsername(traineeUsername)).thenReturn(trainee);
		when(loadTrainerPort.findAllByUsernames(trainerUsernames)).thenReturn(List.of());

		UpdateTraineeTrainersCommand command = new UpdateTraineeTrainersCommand(traineeUsername, trainerUsernames);

		assertThrows(TrainerNotFoundException.class, () -> traineeService.updateTraineeTrainers(command));

		verify(updateTrainingPort, never()).deleteByTraineeId(any());
		verify(updateTraineePort, never()).save(any());
	}
}
