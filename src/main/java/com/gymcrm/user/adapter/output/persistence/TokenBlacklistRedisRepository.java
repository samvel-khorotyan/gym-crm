package com.gymcrm.user.adapter.output.persistence;

import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TokenBlacklistRedisRepository implements TokenBlacklistPort {
	private final StringRedisTemplate redisTemplate;

	@Override
	public void blacklistToken(String token, long expirationTime) {
		redisTemplate.opsForValue().set(token, "blacklisted", expirationTime, TimeUnit.MILLISECONDS);
	}

	@Override
	public boolean isTokenBlacklisted(String token) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(token));
	}
}
