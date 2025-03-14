package com.gymcrm.unit.user.adapter.output.persistence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.gymcrm.user.adapter.output.persistence.TokenBlacklistRedisRepository;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistRedisRepositoryTest {
	@Mock
	private StringRedisTemplate redisTemplate;

	@Mock
	private ValueOperations<String, String> valueOperations;

	@InjectMocks
	private TokenBlacklistRedisRepository tokenBlacklistRedisRepository;

	private final String token = "testToken";

	@Test
	void shouldBlacklistTokenSuccessfully() {
		long expirationTime = 3600000L; // 1 ժամ
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		tokenBlacklistRedisRepository.blacklistToken(token, expirationTime);

		verify(valueOperations, times(1)).set(token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
	}

	@Test
  void shouldReturnTrueWhenTokenIsBlacklisted() {
    when(redisTemplate.hasKey(token)).thenReturn(true);

    boolean result = tokenBlacklistRedisRepository.isTokenBlacklisted(token);

    assertTrue(result);
    verify(redisTemplate, times(1)).hasKey(token);
  }

	@Test
  void shouldReturnFalseWhenTokenIsNotBlacklisted() {
    when(redisTemplate.hasKey(token)).thenReturn(false);

    boolean result = tokenBlacklistRedisRepository.isTokenBlacklisted(token);

    assertFalse(result);
    verify(redisTemplate, times(1)).hasKey(token);
  }
}
