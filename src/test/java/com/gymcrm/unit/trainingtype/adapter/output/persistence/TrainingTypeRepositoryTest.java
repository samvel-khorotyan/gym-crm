package com.gymcrm.unit.trainingtype.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainingtype.adapter.output.persistence.TrainingTypePersistenceRepository;
import com.gymcrm.trainingtype.adapter.output.persistence.TrainingTypeRepository;
import com.gymcrm.trainingtype.application.exception.TrainingTypeNotFoundException;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRepositoryTest {
	@Mock
	private TrainingTypePersistenceRepository repository; // Մոդելացված (Mock) Repository

	@InjectMocks
	private TrainingTypeRepository trainingTypeRepository; // Թեստավորվող իրական Repository

	@Test
	void save_ShouldSaveTrainingType_WhenValidTrainingTypeProvided() {
		TrainingType trainingType = new TrainingType(); // Կեղծ TrainingType օբյեկտ
		when(repository.save(trainingType)).thenReturn(trainingType);

		TrainingType savedTrainingType = trainingTypeRepository.save(trainingType);

		assertEquals(trainingType, savedTrainingType);
		verify(repository, times(1)).save(trainingType);
	}

	@Test
	void findAll_ShouldReturnAllTrainingTypes() {
		List<TrainingType> trainingTypes = List.of(new TrainingType(), new TrainingType());
		when(repository.findAll()).thenReturn(trainingTypes);

		List<TrainingType> result = trainingTypeRepository.findAll();

		assertEquals(trainingTypes, result);
		verify(repository, times(1)).findAll();
	}

	@Test
	void findByTrainingTypeName_ShouldReturnTrainingType_WhenTrainingTypeNameExists() {
		String trainingTypeName = "Yoga";
		TrainingType trainingType = new TrainingType();
		when(repository.findByTrainingTypeName(trainingTypeName)).thenReturn(Optional.of(trainingType));

		TrainingType result = trainingTypeRepository.findByTrainingTypeName(trainingTypeName);

		assertEquals(trainingType, result);
		verify(repository, times(1)).findByTrainingTypeName(trainingTypeName);
	}

	@Test
	void findByTrainingTypeName_ShouldThrowException_WhenTrainingTypeNameNotExists() {
		String trainingTypeName = "NonExistent";
		when(repository.findByTrainingTypeName(trainingTypeName)).thenReturn(Optional.empty());

		assertThrows(TrainingTypeNotFoundException.class,
		        () -> trainingTypeRepository.findByTrainingTypeName(trainingTypeName));
		verify(repository, times(1)).findByTrainingTypeName(trainingTypeName);
	}
}
