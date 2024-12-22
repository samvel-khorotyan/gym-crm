package com.gymcrm.unit.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainee.application.factory.TraineeFactory;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.user.domain.User;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraineeFactoryTest {
  @Mock private UUIDGeneratorInterface uuidGeneratorInterface;

  @InjectMocks private TraineeFactory traineeFactory;

  @Test
  void testCreateFrom_createsTraineeWithCorrectFields() {
    UUID generatedUUID = UUID.randomUUID();
    LocalDate dateOfBirth = LocalDate.of(2000, 1, 1);
    String address = "123 Main St";
    UUID userId = UUID.randomUUID();

    User user = new User(userId, "John", "Doe", "john.doe", "password123", true, null);
    CreateTraineeCommand command = new CreateTraineeCommand(dateOfBirth, address, user);

    when(uuidGeneratorInterface.newUUID()).thenReturn(generatedUUID);

    Trainee trainee = traineeFactory.createFrom(command);

    assertNotNull(trainee, "Trainee object should not be null");
    assertEquals(generatedUUID, trainee.getId(), "Trainee ID should match generated UUID");
    assertEquals(dateOfBirth, trainee.getDateOfBirth(), "Date of Birth should match command");
    assertEquals(address, trainee.getAddress(), "Address should match command");
    assertEquals(user, trainee.getUser(), "User should match command");

    verify(uuidGeneratorInterface, times(1)).newUUID();
  }

  @Test
  void testCreateFrom_handlesNullCommand() {
    assertThrows(
        NullPointerException.class,
        () -> traineeFactory.createFrom(null),
        "Should throw NullPointerException when command is null");
  }
}
