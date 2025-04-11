package com.gymcrm.unit.user.application.factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.user.application.factory.UserFactory;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserFactoryTest {
	@Mock
	private UUIDGeneratorInterface uuidGenerator;

	@InjectMocks
	private UserFactory userFactory;

	@Test
	void createFrom_ShouldReturnUser_WhenValidCommandProvided() {
		UUID uuid = UUID.randomUUID();
		CreateUserCommand command = new CreateUserCommand("John", "Doe", "john.doe", "password123", UserType.TRAINER);
		when(uuidGenerator.newUUID()).thenReturn(uuid);

		User user = userFactory.createFrom(command);

		assertNotNull(user);
		assertEquals(uuid, user.getId());
		assertEquals(command.getFirstName(), user.getFirstName());
		assertEquals(command.getLastName(), user.getLastName());
		assertEquals(command.getUsername(), user.getUsername());
		assertEquals(command.getPassword(), user.getPassword());
		assertTrue(user.getIsActive());
		assertEquals(command.getUserType(), user.getUserType());

		verify(uuidGenerator, times(1)).newUUID();
	}

	@Test
	void createFrom_ShouldHandleEmptyFieldsGracefully() {
		UUID uuid = UUID.randomUUID();
		CreateUserCommand command = new CreateUserCommand("fName", "lName", UserType.TRAINEE);
		when(uuidGenerator.newUUID()).thenReturn(uuid);

		User user = userFactory.createFrom(command);

		assertNotNull(user);
		assertEquals(uuid, user.getId());
		assertEquals("fName", user.getFirstName());
		assertEquals("lName", user.getLastName());
		assertNull(user.getUsername());
		assertNull(user.getPassword());
		assertTrue(user.getIsActive());
		assertEquals(UserType.TRAINEE, user.getUserType());

		verify(uuidGenerator, times(1)).newUUID();
	}
}
