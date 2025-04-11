package com.gymcrm.user.application;

import com.gymcrm.user.application.port.input.BlacklistTokenUseCase;
import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService implements BlacklistTokenUseCase {
	private final TokenBlacklistPort tokenBlacklistPort;

	@Override
	public void blacklistToken(String token, long expirationTime) {
		tokenBlacklistPort.blacklistToken(token, expirationTime);
	}
}
