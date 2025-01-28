package com.gymcrm.user.application;

import com.gymcrm.common.exception.UnauthorizedException;
import com.gymcrm.user.application.port.input.AuthenticationUseCase;
import com.gymcrm.user.application.port.output.AuthenticationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements AuthenticationUseCase {
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	private final AuthenticationPort authenticationPort;

	public AuthenticationService(AuthenticationPort AuthenticationPort) {
		this.authenticationPort = AuthenticationPort;
	}

	@Override
	public void authenticate(String username, String password) {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Authenticating user: {}", transactionId, username);

		try {
			if (authenticationPort.userExistsByCredentials(username, password)) {
				logger.info("Transaction ID: {} - Authentication successful for user: {}", transactionId, username);
				return;
			}

			throw new UnauthorizedException("Authentication failed. Please verify your credentials.");
		} catch (UnauthorizedException e) {
			logger.warn("Transaction ID: {} - Unauthorized access attempt for username: {}", transactionId, username);
			throw e;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Error during authentication for username: {}, Reason: {}", transactionId,
			        username, e.getMessage(), e);
			throw new RuntimeException("Authentication process failed.", e);
		}
	}
}
