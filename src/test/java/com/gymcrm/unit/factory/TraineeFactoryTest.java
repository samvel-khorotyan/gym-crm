package com.gymcrm.unit.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.CreateTraineeCommand;
import com.gymcrm.domain.Trainee;
import com.gymcrm.factory.TraineeFactory;
import com.gymcrm.util.UUIDGeneratorInterface;
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
    String firstName = "John";
    String lastName = "Doe";
    UUID userId = UUID.randomUUID();

    CreateTraineeCommand command = new CreateTraineeCommand();
    command.setAddress(address);
    command.setDateOfBirth(dateOfBirth);
    command.setUserFirstName(firstName);
    command.setUserLastName(lastName);
    command.setUserId(userId);

    when(uuidGeneratorInterface.newUUID()).thenReturn(generatedUUID);

    Trainee trainee = traineeFactory.createFrom(command);

    assertNotNull(trainee, "Trainee object should not be null");
    assertEquals(generatedUUID, trainee.getId(), "Trainee ID should match generated UUID");
    assertEquals(dateOfBirth, trainee.getDateOfBirth(), "Date of Birth should match command");
    assertEquals(address, trainee.getAddress(), "Address should match command");
    assertEquals(userId, trainee.getUserId(), "User ID should match command");

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
