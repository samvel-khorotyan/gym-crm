package com.gymcrm.user.application.port.output;

public interface TokenBlacklistPort {
	void blacklistToken(String token, long expirationTime);

	boolean isTokenBlacklisted(String token);
}
