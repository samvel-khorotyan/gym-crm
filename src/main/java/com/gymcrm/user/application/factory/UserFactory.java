package com.gymcrm.user.application.factory;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.user.application.port.input.CreateUserCommand;
import com.gymcrm.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFactory {
	private final UUIDGeneratorInterface uuidGenerator;

	public User createFrom(CreateUserCommand command) {
		return new User(uuidGenerator.newUUID(), command.getFirstName(), command.getLastName(), command.getUsername(),
		        command.getPassword(), true, command.getUserType());
	}
}
