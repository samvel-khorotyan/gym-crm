package com.gymcrm.user.application.port.input;

public interface AuthenticationUseCase {
	void authenticate(String username, String password);
}
