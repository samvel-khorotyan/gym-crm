package com.gymcrm.configuration.security.protection;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
public class BruteForceProtectionService {
	private static final int MAX_ATTEMPTS = 3;
	private static final long LOCK_TIME_DURATION = 5 * 60;
	private final Map<String, FailedLoginAttempt> attemptsCache = new ConcurrentHashMap<>();

	public void loginFailed(String username) {
		FailedLoginAttempt attempt = attemptsCache.getOrDefault(username, new FailedLoginAttempt());
		attempt.incrementAttempts();
		if (attempt.getAttempts() >= MAX_ATTEMPTS) {
			attempt.setLockTime(LocalDateTime.now().plusSeconds(LOCK_TIME_DURATION));
		}
		attemptsCache.put(username, attempt);
	}

	public boolean isBlocked(String username) {
		FailedLoginAttempt attempt = attemptsCache.get(username);
		if (attempt == null)
			return false;

		if (attempt.getLockTime() != null && LocalDateTime.now().isBefore(attempt.getLockTime())) {
			System.out.println("User " + username + " is still blocked!"); // Debugging
			return true;
		}

		if (attempt.getLockTime() != null && LocalDateTime.now().isAfter(attempt.getLockTime())) {
			System.out.println("User " + username + " block expired, allowing login."); // Debugging
			attemptsCache.remove(username);
		}

		return false;
	}

	public void loginSucceeded(String username) {
		FailedLoginAttempt attempt = attemptsCache.get(username);

		if (attempt != null && attempt.getLockTime() != null && LocalDateTime.now().isBefore(attempt.getLockTime())) {
			System.out.println("User " + username + " is still blocked and cannot log in.");
			return;
		}

		attemptsCache.remove(username);
	}

	@Getter
	private static class FailedLoginAttempt {
		private int attempts;
		@Setter
		private LocalDateTime lockTime;

		public void incrementAttempts() {
			this.attempts++;
		}
	}
}
