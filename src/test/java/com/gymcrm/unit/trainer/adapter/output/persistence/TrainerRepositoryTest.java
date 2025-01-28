package com.gymcrm.unit.trainer.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainer.adapter.output.persistence.TrainerPersistenceRepository;
import com.gymcrm.trainer.adapter.output.persistence.TrainerRepository;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.domain.Trainer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryTest {
	@Mock
	private TrainerPersistenceRepository repository;

	@InjectMocks
	private TrainerRepository trainerRepository;

	@Test
	void save_ShouldSaveTrainer() {
		Trainer trainer = new Trainer();
		when(repository.save(trainer)).thenReturn(trainer);

		Trainer savedTrainer = trainerRepository.save(trainer);

		assertNotNull(savedTrainer);
		verify(repository, times(1)).save(trainer);
	}

	@Test
	void findByIdWithTrainees_ShouldReturnTrainer_WhenExists() {
		UUID id = UUID.randomUUID();
		Trainer trainer = new Trainer();
		when(repository.findByIdWithTrainees(id)).thenReturn(Optional.of(trainer));

		Trainer result = trainerRepository.findByIdWithTrainees(id);

		assertNotNull(result);
		verify(repository, times(1)).findByIdWithTrainees(id);
	}

	@Test
	void findByIdWithTrainees_ShouldThrowException_WhenNotFound() {
		UUID id = UUID.randomUUID();
		when(repository.findByIdWithTrainees(id)).thenReturn(Optional.empty());

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainerRepository.findByIdWithTrainees(id));

		assertEquals("Trainer not found by trainer ID: " + id, exception.getMessage());
		verify(repository, times(1)).findByIdWithTrainees(id);
	}

	@Test
	void findAllByUsernames_ShouldReturnTrainers_WhenExist() {
		List<String> usernames = List.of("trainer1", "trainer2");
		List<Trainer> trainers = List.of(new Trainer(), new Trainer());
		when(repository.findAllByUsernames(usernames)).thenReturn(trainers);

		List<Trainer> result = trainerRepository.findAllByUsernames(usernames);

		assertEquals(trainers.size(), result.size());
		verify(repository, times(1)).findAllByUsernames(usernames);
	}

	@Test
	void findByUsernameWithTrainees_ShouldReturnTrainer_WhenExists() {
		String username = "trainer1";
		Trainer trainer = new Trainer();
		when(repository.findByUsernameWithTrainees(username)).thenReturn(Optional.of(trainer));

		Trainer result = trainerRepository.findByUsernameWithTrainees(username);

		assertNotNull(result);
		verify(repository, times(1)).findByUsernameWithTrainees(username);
	}

	@Test
	void findByUsernameWithTrainees_ShouldThrowException_WhenNotFound() {
		String username = "trainer1";
		when(repository.findByUsernameWithTrainees(username)).thenReturn(Optional.empty());

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainerRepository.findByUsernameWithTrainees(username));

		assertEquals("Trainer not found by username: " + username, exception.getMessage());
		verify(repository, times(1)).findByUsernameWithTrainees(username);
	}

	@Test
	void findByUsername_ShouldReturnTrainer_WhenExists() {
		String username = "trainer1";
		Trainer trainer = new Trainer();
		when(repository.findByUserUsername(username)).thenReturn(Optional.of(trainer));

		Trainer result = trainerRepository.findByUsername(username);

		assertNotNull(result);
		verify(repository, times(1)).findByUserUsername(username);
	}

	@Test
	void findByUsername_ShouldThrowException_WhenNotFound() {
		String username = "trainer1";
		when(repository.findByUserUsername(username)).thenReturn(Optional.empty());

		TrainerNotFoundException exception = assertThrows(TrainerNotFoundException.class,
		        () -> trainerRepository.findByUsername(username));

		assertEquals("Trainer not found by username: " + username, exception.getMessage());
		verify(repository, times(1)).findByUserUsername(username);
	}

	@Test
	void findAll_ShouldReturnAllTrainers() {
		List<Trainer> trainers = List.of(new Trainer(), new Trainer());
		when(repository.findAll()).thenReturn(trainers);

		List<Trainer> result = trainerRepository.findAll();

		assertEquals(trainers.size(), result.size());
		verify(repository, times(1)).findAll();
	}

	@Test
	void findActiveTrainersNotAssignedToTrainee_ShouldReturnActiveTrainers() {
		String traineeUsername = "trainee1";
		List<Trainer> trainers = List.of(new Trainer(), new Trainer());
		when(repository.findActiveTrainersNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);

		List<Trainer> result = trainerRepository.findActiveTrainersNotAssignedToTrainee(traineeUsername);

		assertEquals(trainers.size(), result.size());
		verify(repository, times(1)).findActiveTrainersNotAssignedToTrainee(traineeUsername);
	}
}
