package com.gymcrm.unit.trainer.application.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainer.application.factory.TrainerFactory;
import com.gymcrm.trainer.application.port.input.CreateTrainerCommand;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.domain.User;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerFactoryTest {
	@Mock
	private UUIDGeneratorInterface uuidGenerator;

	@InjectMocks
	private TrainerFactory trainerFactory;

	private CreateTrainerCommand command;
	private UUID mockUUID;

	@BeforeEach
	void setUp() {
		mockUUID = UUID.randomUUID();
		User mockUser = new User();
		command = new CreateTrainerCommand("John", "Doe", "Strength Training", mockUser);
	}

	@Test
  void createFrom_ShouldReturnTrainerWithCorrectValues() {
    when(uuidGenerator.newUUID()).thenReturn(mockUUID);

    Trainer trainer = trainerFactory.createFrom(command);

    assertEquals(mockUUID, trainer.getId());
    assertEquals("Strength Training", trainer.getSpecialization());
    assertEquals(command.getUser(), trainer.getUser());
  }
}
