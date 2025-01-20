package com.gymcrm.unit.training.application.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.application.factory.TrainingFactory;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.domain.Training;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingFactoryTest {
	@Mock
	private UUIDGeneratorInterface uuidGenerator;

	@InjectMocks
	private TrainingFactory trainingFactory;

	@Test
	void createFrom_ShouldCreateTrainingSuccessfully() {
		UUID generatedUUID = UUID.randomUUID();
		when(uuidGenerator.newUUID()).thenReturn(generatedUUID);

		Trainee trainee = new Trainee();
		Trainer trainer = new Trainer();
		String trainingName = "Yoga Basics";
		TrainingType trainingType = new TrainingType();
		LocalDate trainingDate = LocalDate.of(2025, 1, 20);
		Integer trainingDuration = 60;

		CreateTrainingCommand command = new CreateTrainingCommand(trainee, trainer, trainingName, trainingType,
		        trainingDate, trainingDuration);

		Training training = trainingFactory.createFrom(command);

		assertEquals(generatedUUID, training.getId());
		assertEquals(trainingName, training.getTrainingName());
		assertEquals(trainee, training.getTrainee());
		assertEquals(trainer, training.getTrainer());
		assertEquals(trainingType, training.getTrainingType());
		assertEquals(trainingDate, training.getTrainingDate());
		assertEquals(trainingDuration, training.getTrainingDuration());
	}
}
