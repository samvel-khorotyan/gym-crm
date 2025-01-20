package com.gymcrm.unit.trainer.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainer.adapter.input.web.mapper.TrainerUpdateMapper;
import com.gymcrm.trainer.application.TrainerService;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.factory.TrainerFactory;
import com.gymcrm.trainer.application.port.input.ActivateDeactivateTrainerCommand;
import com.gymcrm.trainer.application.port.input.CreateTrainerCommand;
import com.gymcrm.trainer.application.port.input.UpdateTrainerCommand;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.application.port.output.UpdateTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.adapter.input.web.mapper.UserUpdateMapper;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.application.port.input.UpdateUserCommand;
import com.gymcrm.user.application.port.input.UserCreationUseCase;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {
	@Mock
	private UpdateTrainerPort updateTrainerPort;

	@Mock
	private TrainerFactory trainerFactory;

	@Mock
	private LoadTrainerPort loadTrainerPort;

	@Mock
	private UserCreationUseCase userCreationUseCase;

	@Mock
	private UserUpdateMapper userUpdateMapper;

	@Mock
	private TrainerUpdateMapper trainerUpdateMapper;

	@Mock
	private UpdateUserPort updateUserPort;

	@InjectMocks
	private TrainerService trainerService;

	private CreateTrainerCommand validCommand;
	private User mockUser;
	private Trainer mockTrainer;

	@BeforeEach
	void setUp() {
		mockUser = new User(UUID.randomUUID(), "John", "Doe", "john.doe", "password", true, UserType.TRAINER);
		validCommand = new CreateTrainerCommand("John", "Doe", "Strength Training");
		mockTrainer = new Trainer(UUID.randomUUID(), "Strength Training", mockUser);

		MDC.put("transactionId", "test-transaction-id");

		trainerService.setLoadTrainerPort(loadTrainerPort);
	}

	@AfterEach
	void teardown() {
		MDC.clear();
	}

	@Test
  void create_ShouldReturnTrainer_WhenSuccessful() {
    when(userCreationUseCase.create(any(CreateUserCommand.class))).thenReturn(mockUser);
    when(trainerFactory.createFrom(validCommand)).thenReturn(mockTrainer);
    when(updateTrainerPort.save(mockTrainer)).thenReturn(mockTrainer);

    Trainer createdTrainer = trainerService.create(validCommand);

    assertNotNull(createdTrainer);
    assertEquals(mockTrainer.getId(), createdTrainer.getId());
    assertEquals(mockTrainer.getSpecialization(), createdTrainer.getSpecialization());
    verify(userCreationUseCase).create(any(CreateUserCommand.class));
    verify(trainerFactory).createFrom(validCommand);
    verify(updateTrainerPort).save(mockTrainer);
  }

	@Test
  void create_ShouldThrowRuntimeException_WhenUserCreationFails() {
    when(userCreationUseCase.create(any(CreateUserCommand.class)))
        .thenThrow(new RuntimeException("User creation failed"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> trainerService.create(validCommand));
    assertEquals("Failed to create trainer", exception.getMessage());
    verify(userCreationUseCase).create(any(CreateUserCommand.class));
    verifyNoInteractions(trainerFactory);
    verifyNoInteractions(updateTrainerPort);
  }

	@Test
  void create_ShouldThrowRuntimeException_WhenTrainerCreationFails() {
    when(userCreationUseCase.create(any(CreateUserCommand.class))).thenReturn(mockUser);
    when(trainerFactory.createFrom(validCommand))
        .thenThrow(new RuntimeException("Trainer creation failed"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> trainerService.create(validCommand));
    assertEquals("Failed to create trainer", exception.getMessage());
    verify(userCreationUseCase).create(any(CreateUserCommand.class));
    verify(trainerFactory).createFrom(validCommand);
    verifyNoInteractions(updateTrainerPort);
  }

	@Test
  void create_ShouldThrowRuntimeException_WhenTrainerSaveFails() {
    when(userCreationUseCase.create(any(CreateUserCommand.class))).thenReturn(mockUser);
    when(trainerFactory.createFrom(validCommand)).thenReturn(mockTrainer);
    when(updateTrainerPort.save(mockTrainer))
        .thenThrow(new RuntimeException("Trainer save failed"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> trainerService.create(validCommand));
    assertEquals("Failed to create trainer", exception.getMessage());
    verify(userCreationUseCase).create(any(CreateUserCommand.class));
    verify(trainerFactory).createFrom(validCommand);
    verify(updateTrainerPort).save(mockTrainer);
  }

	@Test
	void loadAll_ShouldReturnListOfTrainers_WhenDataExists() {
		List<Trainer> trainers = List.of(
		        new Trainer(UUID.randomUUID(), "Fitness", new User("user1", "password1", UserType.TRAINER)),
		        new Trainer(UUID.randomUUID(), "Wrestling", new User("user2", "password2", UserType.TRAINER)));
		when(loadTrainerPort.findAll()).thenReturn(trainers);

		List<Trainer> result = trainerService.loadAll();

		assertEquals(2, result.size());
		assertEquals("Fitness", result.get(0).getSpecialization());
		assertEquals("Wrestling", result.get(1).getSpecialization());
		verify(loadTrainerPort, times(1)).findAll();
	}

	@Test
  void loadAll_ShouldReturnEmptyList_WhenNoDataExists() {
    when(loadTrainerPort.findAll()).thenReturn(List.of());

    List<Trainer> result = trainerService.loadAll();

    assertTrue(result.isEmpty());
    verify(loadTrainerPort, times(1)).findAll();
  }

	@Test
  void loadAll_ShouldThrowRuntimeException_WhenLoadFails() {
    when(loadTrainerPort.findAll()).thenThrow(new RuntimeException("Database error"));

    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> trainerService.loadAll());
    assertEquals("Failed to fetch trainers", exception.getMessage());
    verify(loadTrainerPort, times(1)).findAll();
  }

	@Test
	void loadActiveTrainersNotAssignedToTrainee_ShouldReturnTrainers_WhenDataExists() {
		String traineeUsername = "trainee1";
		List<Trainer> trainers = List.of(
		        new Trainer(UUID.randomUUID(), "Fitness", new User("user1", "password1", UserType.TRAINER)),
		        new Trainer(UUID.randomUUID(), "Wrestling", new User("user2", "password2", UserType.TRAINER)));
		when(loadTrainerPort.findActiveTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);

		List<Trainer> result = trainerService.loadActiveTrainersNotAssignedToTrainee(traineeUsername);

		assertEquals(2, result.size());
		assertEquals("Fitness", result.get(0).getSpecialization());
		assertEquals("Wrestling", result.get(1).getSpecialization());
		verify(loadTrainerPort, times(1)).findActiveTrainersNotAssignedToTrainee(traineeUsername);
	}

	@Test
	void loadActiveTrainersNotAssignedToTrainee_ShouldReturnEmptyList_WhenNoDataExists() {
		String traineeUsername = "trainee1";
		when(loadTrainerPort.findActiveTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(List.of());

		List<Trainer> result = trainerService.loadActiveTrainersNotAssignedToTrainee(traineeUsername);

		assertTrue(result.isEmpty());
		verify(loadTrainerPort, times(1)).findActiveTrainersNotAssignedToTrainee(traineeUsername);
	}

	@Test
	void loadActiveTrainersNotAssignedToTrainee_ShouldThrowRuntimeException_WhenLoadFails() {
		String traineeUsername = "trainee1";
		when(loadTrainerPort.findActiveTrainersNotAssignedToTrainee(traineeUsername))
		        .thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> trainerService.loadActiveTrainersNotAssignedToTrainee(traineeUsername));
		assertEquals("Failed to fetch trainers not assigned to trainee.", exception.getMessage());
		verify(loadTrainerPort, times(1)).findActiveTrainersNotAssignedToTrainee(traineeUsername);
	}

	@Test
	void loadActiveTrainersNotAssignedToTrainee_ShouldLogTransactionId() {
		String traineeUsername = "trainee1";
		List<Trainer> trainers = List
		        .of(new Trainer(UUID.randomUUID(), "Fitness", new User("user1", "password1", UserType.TRAINER)));
		when(loadTrainerPort.findActiveTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);

		trainerService.loadActiveTrainersNotAssignedToTrainee(traineeUsername);

		verify(loadTrainerPort, times(1)).findActiveTrainersNotAssignedToTrainee(traineeUsername);
	}

	@Test
	void loadByUsername_ShouldReturnTrainer_WhenTrainerExists() {
		String username = "trainer1";
		Trainer expectedTrainer = new Trainer(UUID.randomUUID(), "Fitness",
		        new User("trainer1", "password", UserType.TRAINER));
		when(loadTrainerPort.findByUsernameWithTrainees(username)).thenReturn(expectedTrainer);

		Trainer result = trainerService.loadByUsername(username);

		assertNotNull(result);
		assertEquals(expectedTrainer.getSpecialization(), result.getSpecialization());
		assertEquals(expectedTrainer.getUser().getUsername(), result.getUser().getUsername());
		verify(loadTrainerPort, times(1)).findByUsernameWithTrainees(username);
	}

	@Test
	void loadByUsername_ShouldThrowTrainerNotFoundException_WhenTrainerDoesNotExist() {
		String username = "trainer2";
		when(loadTrainerPort.findByUsernameWithTrainees(username))
		        .thenThrow(new TrainerNotFoundException("Trainer not found"));

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainerService.loadByUsername(username));
		assertEquals("Trainer not found", exception.getMessage());
		verify(loadTrainerPort, times(1)).findByUsernameWithTrainees(username);
	}

	@Test
	void loadByUsername_ShouldThrowRuntimeException_WhenLoadFails() {
		String username = "trainer3";
		when(loadTrainerPort.findByUsernameWithTrainees(username)).thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> trainerService.loadByUsername(username));
		assertEquals("Failed to fetch trainer details.", exception.getMessage());
		verify(loadTrainerPort, times(1)).findByUsernameWithTrainees(username);
	}

	@Test
	void loadByUsername_ShouldLogTransactionId_WhenFetchingTrainer() {
		String username = "trainer1";
		Trainer expectedTrainer = new Trainer(UUID.randomUUID(), "Wrestling",
		        new User("trainer1", "password", UserType.TRAINER));
		when(loadTrainerPort.findByUsernameWithTrainees(username)).thenReturn(expectedTrainer);

		trainerService.loadByUsername(username);

		verify(loadTrainerPort, times(1)).findByUsernameWithTrainees(username);
	}

	@Test
	void update_ShouldUpdateTrainerSuccessfully_WhenTrainerExists() {
		UUID trainerId = UUID.randomUUID();
		UpdateTrainerCommand command = new UpdateTrainerCommand(trainerId, "John", "Doe", "Fitness", true);
		Trainer existingTrainer = new Trainer(trainerId, "Wrestling",
		        new User("trainer1", "password", UserType.TRAINER));
		Trainer updatedTrainer = new Trainer(trainerId, "Fitness", new User("trainer1", "password", UserType.TRAINER));

		when(loadTrainerPort.findByIdWithTrainees(trainerId)).thenReturn(existingTrainer);
		when(updateTrainerPort.save(existingTrainer)).thenReturn(updatedTrainer);

		Trainer result = trainerService.update(command);

		assertNotNull(result);
		assertEquals("Fitness", result.getSpecialization());
		verify(loadTrainerPort, times(1)).findByIdWithTrainees(trainerId);
		verify(userUpdateMapper, times(1)).updateUserFromCommand(
		        new UpdateUserCommand(command.getFirstName(), command.getLastName(), command.getIsActive()),
		        existingTrainer.getUser());
		verify(trainerUpdateMapper, times(1)).updateTrainerFromCommand(command, existingTrainer);
		verify(updateTrainerPort, times(1)).save(existingTrainer);
	}

	@Test
	void update_ShouldThrowTrainerNotFoundException_WhenTrainerDoesNotExist() {
		UUID trainerId = UUID.randomUUID();
		UpdateTrainerCommand command = new UpdateTrainerCommand(trainerId, "John", "Doe", "Fitness", true);

		when(loadTrainerPort.findByIdWithTrainees(trainerId))
		        .thenThrow(new TrainerNotFoundException("Trainer not found"));

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainerService.update(command));
		assertEquals("Trainer not found", exception.getMessage());
		verify(loadTrainerPort, times(1)).findByIdWithTrainees(trainerId);
		verifyNoInteractions(userUpdateMapper, trainerUpdateMapper, updateTrainerPort);
	}

	@Test
	void update_ShouldThrowRuntimeException_WhenUpdateFails() {
		UUID trainerId = UUID.randomUUID();
		UpdateTrainerCommand command = new UpdateTrainerCommand(trainerId, "John", "Doe", "Fitness", true);
		Trainer existingTrainer = new Trainer(trainerId, "Wrestling",
		        new User("trainer1", "password", UserType.TRAINER));

		when(loadTrainerPort.findByIdWithTrainees(trainerId)).thenReturn(existingTrainer);
		doThrow(new RuntimeException("Database error")).when(updateTrainerPort).save(existingTrainer);

		RuntimeException exception = assertThrows(RuntimeException.class, () -> trainerService.update(command));
		assertEquals("Failed to update trainer.", exception.getMessage());
		verify(loadTrainerPort, times(1)).findByIdWithTrainees(trainerId);
		verify(updateTrainerPort, times(1)).save(existingTrainer);
	}

	@Test
	void update_ShouldLogTransactionId_WhenUpdatingTrainer() {
		UUID trainerId = UUID.randomUUID();
		UpdateTrainerCommand command = new UpdateTrainerCommand(trainerId, "John", "Doe", "Fitness", true);
		Trainer existingTrainer = new Trainer(trainerId, "WrestlingWrestling",
		        new User("trainer1", "password", UserType.TRAINER));

		when(loadTrainerPort.findByIdWithTrainees(trainerId)).thenReturn(existingTrainer);
		when(updateTrainerPort.save(existingTrainer)).thenReturn(existingTrainer);

		trainerService.update(command);

		verify(loadTrainerPort, times(1)).findByIdWithTrainees(trainerId);
	}

	@Test
	void activateDeactivate_ShouldActivateTrainerSuccessfully() {
		String username = "trainer1";
		ActivateDeactivateTrainerCommand command = new ActivateDeactivateTrainerCommand(username, true);
		User user = new User(UUID.randomUUID(), "John", "Doe", username, "password", false, UserType.TRAINER);

		when(loadTrainerPort.findByUsername(username)).thenReturn(new Trainer(UUID.randomUUID(), "Fitness", user));

		trainerService.activateDeactivate(command);

		assertTrue(user.getIsActive());
		verify(loadTrainerPort, times(1)).findByUsername(username);
		verify(updateUserPort, times(1)).save(user);
	}

	@Test
	void activateDeactivate_ShouldDeactivateTrainerSuccessfully() {
		String username = "trainer2";
		ActivateDeactivateTrainerCommand command = new ActivateDeactivateTrainerCommand(username, false);
		User user = new User(UUID.randomUUID(), "Jane", "Smith", username, "password", true, UserType.TRAINER);

		when(loadTrainerPort.findByUsername(username)).thenReturn(new Trainer(UUID.randomUUID(), "Yoga", user));

		trainerService.activateDeactivate(command);

		assertFalse(user.getIsActive());
		verify(loadTrainerPort, times(1)).findByUsername(username);
		verify(updateUserPort, times(1)).save(user);
	}

	@Test
	void activateDeactivate_ShouldThrowTrainerNotFoundException_WhenTrainerDoesNotExist() {
		String username = "unknownTrainer";
		ActivateDeactivateTrainerCommand command = new ActivateDeactivateTrainerCommand(username, true);

		when(loadTrainerPort.findByUsername(username)).thenThrow(new TrainerNotFoundException("Trainer not found"));

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainerService.activateDeactivate(command));
		assertEquals("Trainer not found", exception.getMessage());
		verify(loadTrainerPort, times(1)).findByUsername(username);
		verifyNoInteractions(updateUserPort);
	}

	@Test
	void activateDeactivate_ShouldThrowRuntimeException_WhenSaveFails() {
		String username = "trainer3";
		ActivateDeactivateTrainerCommand command = new ActivateDeactivateTrainerCommand(username, true);
		User user = new User(UUID.randomUUID(), "Mike", "Johnson", username, "password", false, UserType.TRAINER);

		when(loadTrainerPort.findByUsername(username)).thenReturn(new Trainer(UUID.randomUUID(), "Boxing", user));
		doThrow(new RuntimeException("Database error")).when(updateUserPort).save(user);

		RuntimeException exception = assertThrows(RuntimeException.class,
		        () -> trainerService.activateDeactivate(command));
		assertEquals(
		        "Transaction ID: test-transaction-id - Failed to activate/deactivate trainer. Cause: Database error",
		        exception.getMessage());
		verify(loadTrainerPort, times(1)).findByUsername(username);
		verify(updateUserPort, times(1)).save(user);
	}

	@Test
	void activateDeactivate_ShouldLogTransactionId_WhenActivatingTrainer() {
		String username = "trainer4";
		ActivateDeactivateTrainerCommand command = new ActivateDeactivateTrainerCommand(username, true);
		User user = new User(UUID.randomUUID(), "Anna", "Taylor", username, "password", false, UserType.TRAINER);

		when(loadTrainerPort.findByUsername(username)).thenReturn(new Trainer(UUID.randomUUID(), "Pilates", user));

		trainerService.activateDeactivate(command);

		verify(loadTrainerPort, times(1)).findByUsername(username);
	}
}
