package com.gymcrm.unit.trainee.application.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainee.application.factory.TraineeFactory;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraineeFactoryTest {
	@Mock
	private UUIDGeneratorInterface uuidGenerator;

	@InjectMocks
	private TraineeFactory traineeFactory;

	@Test
	void createFrom_ShouldReturnTraineeWithCorrectValues() {
		UUID mockUUID = UUID.randomUUID();
		when(uuidGenerator.newUUID()).thenReturn(mockUUID);

		CreateTraineeCommand command = new CreateTraineeCommand("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St",
		        new User(UUID.randomUUID(), "John", "Doe", "john.doe", "password123", true, UserType.TRAINEE));

		Trainee result = traineeFactory.createFrom(command);

		assertNotNull(result);
		assertEquals(mockUUID, result.getId());
		assertEquals(command.getDateOfBirth(), result.getDateOfBirth());
		assertEquals(command.getAddress(), result.getAddress());
		assertEquals(command.getUser(), result.getUser());

		verify(uuidGenerator, times(1)).newUUID();
	}
}
