package com.gymcrm.unit.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.command.CreateUserCommand;
import com.gymcrm.domain.User;
import com.gymcrm.factory.UserFactory;
import com.gymcrm.util.UUIDGeneratorInterface;
import com.gymcrm.util.UserUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {
  @Mock private UUIDGeneratorInterface uuidGeneratorInterface;

  @InjectMocks private UserFactory userFactory;

  @Test
  void testCreateFrom_createsUserWithCorrectFields() {
    UUID generatedUUID = UUID.randomUUID();
    String firstName = "John";
    String lastName = "Doe";
    String expectedUsername = "johndoe";
    String expectedPassword = "randomPassword123";

    CreateUserCommand command = new CreateUserCommand(firstName, lastName);

    when(uuidGeneratorInterface.newUUID()).thenReturn(generatedUUID);

    try (MockedStatic<UserUtil> mockedUserUtil = mockStatic(UserUtil.class)) {
      mockedUserUtil
          .when(() -> UserUtil.generateUsername(firstName, lastName))
          .thenReturn(expectedUsername);
      mockedUserUtil.when(UserUtil::generatePassword).thenReturn(expectedPassword);

      User user = userFactory.createFrom(command);

      assertNotNull(user, "User object should not be null");
      assertEquals(generatedUUID, user.getId(), "User ID should match generated UUID");
      assertEquals(firstName, user.getFirstName(), "First name should match command");
      assertEquals(lastName, user.getLastName(), "Last name should match command");
      assertEquals(
          expectedUsername, user.getUsername(), "Username should match generated username");
      assertEquals(
          expectedPassword, user.getPassword(), "Password should match generated password");
      assertTrue(user.getIsActive(), "User should be active by default");

      verify(uuidGeneratorInterface, times(1)).newUUID();
      mockedUserUtil.verify(() -> UserUtil.generateUsername(firstName, lastName), times(1));
      mockedUserUtil.verify(UserUtil::generatePassword, times(1));
    }
  }

  @Test
  void testCreateFrom_handlesNullCommand() {
    assertThrows(
        NullPointerException.class,
        () -> userFactory.createFrom(null),
        "Should throw NullPointerException when command is null");
  }
}
