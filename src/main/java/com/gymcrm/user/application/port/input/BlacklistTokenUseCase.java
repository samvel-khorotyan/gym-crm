package com.gymcrm.user.application.port.input;

public interface BlacklistTokenUseCase {
	void blacklistToken(String token, long expirationTime);
}
