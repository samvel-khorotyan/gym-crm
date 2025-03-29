package com.gymcrm.configuration.messaging.health;

import javax.jms.Connection;
import javax.jms.JMSException;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JmsHealthIndicator implements HealthIndicator {
	private final ActiveMQConnectionFactory connectionFactory;

	@Override
	public Health health() {
		try (Connection connection = connectionFactory.createConnection()) {
			connection.start();
			return Health.up().withDetail("status", "ActiveMQ connection is established").build();
		} catch (JMSException e) {
			return Health.down().withDetail("error", e.getMessage()).build();
		}
	}
}
