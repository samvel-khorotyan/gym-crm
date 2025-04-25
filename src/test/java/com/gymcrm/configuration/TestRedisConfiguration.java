package com.gymcrm.configuration;

import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Configuration
@Profile("testing")
public class TestRedisConfiguration {
	private RedisServer redisServer;

	@Bean
	public RedisServer redisServer() {
		redisServer = new RedisServer(6379);
		redisServer.start();
		return redisServer;
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}
}
