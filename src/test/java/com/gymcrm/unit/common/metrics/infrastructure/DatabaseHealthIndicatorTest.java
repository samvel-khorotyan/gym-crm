package com.gymcrm.unit.common.metrics.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.gymcrm.common.metrics.infrastructure.DatabaseHealthIndicator;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {
	@Mock
	private DataSource dataSource;

	@Mock
	private Connection connection;

	@InjectMocks
	private DatabaseHealthIndicator databaseHealthIndicator;

	@Test
  void testHealth_DatabaseIsUp() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.isValid(1)).thenReturn(true);

    Health health = databaseHealthIndicator.health();

    assertEquals(Health.up().withDetail("Database", "Available").build(), health);
    verify(dataSource, times(1)).getConnection();
    verify(connection, times(1)).isValid(1);
    verify(connection, times(1)).close();
  }

	@Test
  void testHealth_DatabaseIsDown() throws SQLException {
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.isValid(1)).thenReturn(false);

    Health health = databaseHealthIndicator.health();

    assertEquals(Health.down().withDetail("Database", "Not Available").build(), health);
    verify(dataSource, times(1)).getConnection();
    verify(connection, times(1)).isValid(1);
    verify(connection, times(1)).close();
  }

	@Test
  void testHealth_DatabaseConnectionThrowsException() throws SQLException {
    when(dataSource.getConnection()).thenThrow(new SQLException("Test Exception"));

    Health health = databaseHealthIndicator.health();

    assertEquals(Health.down().withDetail("Database", "Not Available").build(), health);
    verify(dataSource, times(1)).getConnection();
  }
}
