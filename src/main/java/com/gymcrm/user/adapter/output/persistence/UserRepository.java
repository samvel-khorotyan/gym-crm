package com.gymcrm.user.adapter.output.persistence;

import com.gymcrm.user.application.exception.UserNotFoundException;
import com.gymcrm.user.application.port.output.AuthenticationPort;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.application.port.output.UpdateUserPort;
import com.gymcrm.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UpdateUserPort, LoadUserPort, AuthenticationPort {
	private final UserPersistenceRepository repository;

	@Override
	public User save(User user) {
		return repository.save(user);
	}

	@Override
	public List<String> findDistinctUsernamesStartingWith(String baseUsername) {
		return repository.findDistinctUsernamesStartingWith(baseUsername);
	}

	@Override
	public User findByUsername(String username) {
		return repository.findByUsername(username).orElseThrow(() -> UserNotFoundException.by(username));
	}

	@Override
	public List<User> findAll() {
		return repository.findAll();
	}

	@Override
	public boolean userExistsByCredentials(String username, String password) {
		return repository.existsByUsernameAndPassword(username, password);
	}
}
