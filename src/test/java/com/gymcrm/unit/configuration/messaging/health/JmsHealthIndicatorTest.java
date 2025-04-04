package com.gymcrm.unit.configuration.messaging.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import com.gymcrm.configuration.messaging.health.JmsHealthIndicator;
import javax.jms.Connection;
import javax.jms.JMSException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
class JmsHealthIndicatorTest {
	@Mock
	private ActiveMQConnectionFactory connectionFactory;

	@Mock
	private Connection connection;

	@InjectMocks
	private JmsHealthIndicator jmsHealthIndicator;

	@Test
  void health_ShouldReturnUp_WhenConnectionIsSuccessful() throws JMSException {
    when(connectionFactory.createConnection()).thenReturn(connection);
    doNothing().when(connection).start();

    Health health = jmsHealthIndicator.health();

    assertEquals(Status.UP, health.getStatus());
    assertTrue(health.getDetails().containsKey("status"));
    assertEquals("ActiveMQ connection is established", health.getDetails().get("status"));

    verify(connectionFactory).createConnection();
    verify(connection).start();
    verify(connection).close();
  }

	@Test
	void health_ShouldReturnDown_WhenConnectionFactoryThrowsException() throws JMSException {
		JMSException jmsException = new JMSException("Connection failed");
		when(connectionFactory.createConnection()).thenThrow(jmsException);

		Health health = jmsHealthIndicator.health();

		assertEquals(Status.DOWN, health.getStatus());
		assertTrue(health.getDetails().containsKey("error"));
		assertEquals("Connection failed", health.getDetails().get("error"));

		verify(connectionFactory).createConnection();
		verify(connection, never()).start();
		verify(connection, never()).close();
	}

	@Test
  void health_ShouldReturnDown_WhenConnectionStartThrowsException() throws JMSException {
    when(connectionFactory.createConnection()).thenReturn(connection);
    JMSException jmsException = new JMSException("Failed to start connection");
    doThrow(jmsException).when(connection).start();

    Health health = jmsHealthIndicator.health();

    assertEquals(Status.DOWN, health.getStatus());
    assertTrue(health.getDetails().containsKey("error"));
    assertEquals("Failed to start connection", health.getDetails().get("error"));

    verify(connectionFactory).createConnection();
    verify(connection).start();
    verify(connection).close();
  }

	@Test
  void health_ShouldReturnDown_WhenConnectionCloseThrowsException() throws JMSException {
    when(connectionFactory.createConnection()).thenReturn(connection);
    doNothing().when(connection).start();
    JMSException jmsException = new JMSException("Failed to close connection");
    doThrow(jmsException).when(connection).close();

    Health health = jmsHealthIndicator.health();

    assertEquals(Status.DOWN, health.getStatus());
    assertTrue(health.getDetails().containsKey("error"));
    assertEquals("Failed to close connection", health.getDetails().get("error"));

    verify(connectionFactory).createConnection();
    verify(connection).start();
    verify(connection).close();
  }

	@Test
	void health_ShouldIncludeDetailedErrorInfo_WhenExceptionOccurs() throws JMSException {
		JMSException jmsException = new JMSException("Connection error");
		jmsException.setLinkedException(new RuntimeException("Underlying network issue"));
		when(connectionFactory.createConnection()).thenThrow(jmsException);

		Health health = jmsHealthIndicator.health();

		assertEquals(Status.DOWN, health.getStatus());
		assertTrue(health.getDetails().containsKey("error"));
		assertEquals("Connection error", health.getDetails().get("error"));

		verify(connectionFactory).createConnection();
		verify(connection, never()).start();
		verify(connection, never()).close();
	}
}
