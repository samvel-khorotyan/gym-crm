package com.gymcrm.unit.user.application;

import static org.mockito.Mockito.*;

import com.gymcrm.user.application.TokenBlacklistService;
import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceTest {
	@Mock
	private TokenBlacklistPort tokenBlacklistPort;

	@InjectMocks
	private TokenBlacklistService tokenBlacklistService;

	@Test
	void shouldCallBlacklistTokenMethodWithCorrectArguments() {
		String token = "test-token";
		long expirationTime = 3600L;

		tokenBlacklistService.blacklistToken(token, expirationTime);

		verify(tokenBlacklistPort, times(1)).blacklistToken(token, expirationTime);
	}

	@Test
	void shouldNotCallBlacklistTokenWhenTokenIsNull() {
		String token = null;
		long expirationTime = 3600L;

		tokenBlacklistService.blacklistToken(token, expirationTime);

		verify(tokenBlacklistPort, never()).blacklistToken(anyString(), anyLong());
	}

	@Test
	void shouldCallBlacklistTokenWhenExpirationTimeIsZero() {
		String token = "valid-token";
		long expirationTime = 0L;

		tokenBlacklistService.blacklistToken(token, expirationTime);

		verify(tokenBlacklistPort, times(1)).blacklistToken(token, expirationTime);
	}
}
