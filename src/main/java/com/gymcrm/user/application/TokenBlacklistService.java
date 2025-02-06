package com.gymcrm.user.application;

import com.gymcrm.user.application.port.input.BlacklistTokenUseCase;
import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService implements BlacklistTokenUseCase {
	private final TokenBlacklistPort tokenBlacklistPort;

	public TokenBlacklistService(TokenBlacklistPort tokenBlacklistPort) {
		this.tokenBlacklistPort = tokenBlacklistPort;
	}

	@Override
	public void blacklistToken(String token, long expirationTime) {
		tokenBlacklistPort.blacklistToken(token, expirationTime);
	}
}
