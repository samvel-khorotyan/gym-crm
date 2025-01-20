package com.gymcrm.user.application;

import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.user.application.exception.UserNotFoundException;
import com.gymcrm.user.application.factory.UserFactory;
import com.gymcrm.user.application.port.input.*;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.util.UserUtil;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserCreationUseCase, LoadUserUseCase, UserUpdateUseCase {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserFactory userFactory;
	private final UpdateUserPort updateUserPort;
	private final LoadUserPort loadUserPort;
	private final AuthenticationUseCase authenticationUseCase;

	@Autowired
	public UserService(UserFactory userFactory, UpdateUserPort updateUserPort, LoadUserPort loadUserPort,
	        AuthenticationUseCase authenticationUseCase) {
		this.userFactory = userFactory;
		this.updateUserPort = updateUserPort;
		this.loadUserPort = loadUserPort;
		this.authenticationUseCase = authenticationUseCase;
	}

	@Override
	public User create(CreateUserCommand command) {
		try {
			if (command.getFirstName() == null || command.getFirstName().isBlank() || command.getLastName() == null
			        || command.getLastName().isBlank()) {
				throw new IllegalArgumentException("First name and last name cannot be null or empty");
			}

			command.setUsername(generateUsername(command.getFirstName(), command.getLastName()));
			command.setPassword(UserUtil.generatePassword());
			return updateUserPort.save(userFactory.createFrom(command));
		} catch (Exception e) {
			logger.error("Error creating user for: {} {}, Reason: {}", command.getFirstName(), command.getLastName(),
			        e.getMessage(), e);
			throw new RuntimeException("Failed to create user", e);
		}
	}

	@Override
	public User loadUserByUsername(String username) {
		try {
			return loadUserPort.findByUsername(username);
		} catch (UserNotFoundException e) {
			logger.error("Error fetching user by username: {}, Reason: {}", username, e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			logger.error("Unexpected error fetching user by username: {}, Reason: {}", username, e.getMessage(), e);
			throw new RuntimeException("Failed to fetch user by username", e);
		}
	}

	@Override
	public List<User> loadAll() {
		try {
			return loadUserPort.findAll();
		} catch (Exception e) {
			logger.error("Error fetching all users, Reason: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to fetch all users", e);
		}
	}

	@Override
	public void updatePassword(UpdatePasswordCommand command) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Updating password for user: {}", transactionId, command.getUsername());

		try {
			authenticationUseCase.authenticate(command.getUsername(), command.getOldPassword());

			User user = loadUserPort.findByUsername(command.getUsername());

			user.setPassword(command.getNewPassword());
			updateUserPort.save(user);

			logger.info("Transaction ID: {} - Successfully updated password for user: {}", transactionId,
			        command.getUsername());
		} catch (UnauthorizedException | UserNotFoundException e) {
			logger.warn("Transaction ID: {} - Failed to update password for user: {}, Reason: {}", transactionId,
			        command.getUsername(), e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Unexpected error while updating password for user: {}, Reason: {}",
			        transactionId, command.getUsername(), e.getMessage(), e);
			throw new RuntimeException("Unexpected error occurred. Please contact support.", e);
		}
	}

	private String generateUsername(String firstName, String lastName) {
		try {
			String baseUsername = UserUtil.getBaseUsername(firstName, lastName);
			var usernames = loadUserPort.findDistinctUsernamesStartingWith(baseUsername);
			return UserUtil.generateUniqueUsername(new HashSet<>(usernames), baseUsername);
		} catch (Exception e) {
			logger.error("Error generating username for: {} {}, Reason: {}", firstName, lastName, e.getMessage(), e);
			throw new RuntimeException("Failed to generate username", e);
		}
	}
}
