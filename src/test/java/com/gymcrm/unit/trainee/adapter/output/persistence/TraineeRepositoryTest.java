package com.gymcrm.unit.trainee.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.adapter.output.persistence.TraineePersistenceRepository;
import com.gymcrm.trainee.adapter.output.persistence.TraineeRepository;
import com.gymcrm.trainee.application.exception.TraineeNotFoundException;
import com.gymcrm.trainee.domain.Trainee;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryTest {
	@Mock
	private TraineePersistenceRepository repository;

	@InjectMocks
	private TraineeRepository traineeRepository;

	@Test
	void findByIdWithTrainers_ShouldReturnTrainee_WhenTraineeExists() {
		UUID id = UUID.randomUUID();
		Trainee mockTrainee = new Trainee();
		when(repository.findByIdWithTrainers(id)).thenReturn(Optional.of(mockTrainee));

		Trainee result = traineeRepository.findByIdWithTrainers(id);

		assertNotNull(result);
		assertEquals(mockTrainee, result);
		verify(repository, times(1)).findByIdWithTrainers(id);
	}

	@Test
	void findByIdWithTrainers_ShouldThrowException_WhenTraineeDoesNotExist() {
		UUID id = UUID.randomUUID();
		when(repository.findByIdWithTrainers(id)).thenReturn(Optional.empty());

		TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class,
		        () -> traineeRepository.findByIdWithTrainers(id));

		assertTrue(exception.getMessage().contains(id.toString()));
		verify(repository, times(1)).findByIdWithTrainers(id);
	}

	@Test
	void findByUsernameWithTrainers_ShouldReturnTrainee_WhenTraineeExists() {
		String username = "testUser";
		Trainee mockTrainee = new Trainee();
		when(repository.findByUsernameWithTrainers(username)).thenReturn(Optional.of(mockTrainee));

		Trainee result = traineeRepository.findByUsernameWithTrainers(username);

		assertNotNull(result);
		assertEquals(mockTrainee, result);
		verify(repository, times(1)).findByUsernameWithTrainers(username);
	}

	@Test
	void findByUsernameWithTrainers_ShouldThrowException_WhenTraineeDoesNotExist() {
		String username = "testUser";
		when(repository.findByUsernameWithTrainers(username)).thenReturn(Optional.empty());

		TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class,
		        () -> traineeRepository.findByUsernameWithTrainers(username));

		assertTrue(exception.getMessage().contains(username));
		verify(repository, times(1)).findByUsernameWithTrainers(username);
	}

	@Test
	void findByUsername_ShouldReturnTrainee_WhenTraineeExists() {
		String username = "testUser";
		Trainee mockTrainee = new Trainee();
		when(repository.findByUserUsername(username)).thenReturn(Optional.of(mockTrainee));

		Trainee result = traineeRepository.findByUsername(username);

		assertNotNull(result);
		assertEquals(mockTrainee, result);
		verify(repository, times(1)).findByUserUsername(username);
	}

	@Test
	void findByUsername_ShouldThrowException_WhenTraineeDoesNotExist() {
		String username = "testUser";
		when(repository.findByUserUsername(username)).thenReturn(Optional.empty());

		TraineeNotFoundException exception = assertThrows(TraineeNotFoundException.class,
		        () -> traineeRepository.findByUsername(username));

		assertTrue(exception.getMessage().contains(username));
		verify(repository, times(1)).findByUserUsername(username);
	}

	@Test
	void findAll_ShouldReturnListOfTrainees() {
		List<Trainee> mockTrainees = List.of(new Trainee(), new Trainee());
		when(repository.findAll()).thenReturn(mockTrainees);

		List<Trainee> result = traineeRepository.findAll();

		assertNotNull(result);
		assertEquals(2, result.size());
		verify(repository, times(1)).findAll();
	}

	@Test
	void save_ShouldReturnSavedTrainee() {
		Trainee trainee = new Trainee();
		when(repository.save(trainee)).thenReturn(trainee);

		Trainee result = traineeRepository.save(trainee);

		assertNotNull(result);
		assertEquals(trainee, result);
		verify(repository, times(1)).save(trainee);
	}

	@Test
	void deleteByUsername_ShouldCallRepositoryMethod() {
		String username = "testUser";

		traineeRepository.deleteByUsername(username);

		verify(repository, times(1)).deleteByUsername(username);
	}
}
