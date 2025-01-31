package com.gymcrm.common.metrics.infrastructure;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {
	private final DataSource dataSource;

	public DatabaseHealthIndicator(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Health health() {
		boolean dbIsUp = checkDatabaseConnection();
		if (dbIsUp) {
			return Health.up().withDetail("Database", "Available").build();
		} else {
			return Health.down().withDetail("Database", "Not Available").build();
		}
	}

	private boolean checkDatabaseConnection() {
		try (Connection connection = dataSource.getConnection()) {
			return connection.isValid(1);
		} catch (SQLException e) {
			return false;
		}
	}
}
