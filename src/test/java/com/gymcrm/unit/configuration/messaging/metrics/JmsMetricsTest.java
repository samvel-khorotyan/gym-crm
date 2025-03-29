package com.gymcrm.unit.configuration.messaging.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.gymcrm.configuration.messaging.metrics.JmsMetrics;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JmsMetricsTest {
	private MeterRegistry registry;
	private JmsMetrics jmsMetrics;

	@BeforeEach
	void setUp() {
		registry = new SimpleMeterRegistry();
		jmsMetrics = new JmsMetrics(registry);
	}

	@Test
	void shouldInitializeAllMetrics() {
		assertNotNull(jmsMetrics.getMessagesSentCounter());
		assertNotNull(jmsMetrics.getMessagesReceivedCounter());
		assertNotNull(jmsMetrics.getMessagesFailedCounter());
		assertNotNull(jmsMetrics.getMessageProcessingTimer());

		assertEquals("Number of JMS messages sent",
		        Objects.requireNonNull(registry.find("jms.messages.sent").counter()).getId().getDescription());
		assertEquals("Number of JMS messages received",
		        Objects.requireNonNull(registry.find("jms.messages.received").counter()).getId().getDescription());
		assertEquals("Number of JMS messages that failed to process",
		        Objects.requireNonNull(registry.find("jms.messages.failed").counter()).getId().getDescription());
		assertEquals("Time taken to process JMS messages",
		        Objects.requireNonNull(registry.find("jms.messages.processing.time").timer()).getId().getDescription());
	}

	@Test
	void recordMessageSent_ShouldIncrementSentCounter() {
		jmsMetrics.recordMessageSent();
		jmsMetrics.recordMessageSent();

		Counter counter = registry.find("jms.messages.sent").counter();
		assertEquals(2.0, counter.count());
	}

	@Test
	void recordMessageReceived_ShouldIncrementReceivedCounter() {
		jmsMetrics.recordMessageReceived();

		Counter counter = registry.find("jms.messages.received").counter();
		assertEquals(1.0, counter.count());
	}

	@Test
	void recordMessageFailed_ShouldIncrementFailedCounter() {
		jmsMetrics.recordMessageFailed();
		jmsMetrics.recordMessageFailed();
		jmsMetrics.recordMessageFailed();

		Counter counter = registry.find("jms.messages.failed").counter();
		assertEquals(3.0, counter.count());
	}

	@Test
	void recordProcessingTime_ShouldRecordTimeInTimer() {
		jmsMetrics.recordProcessingTime(100);
		jmsMetrics.recordProcessingTime(200);

		Timer timer = registry.find("jms.messages.processing.time").timer();
		assertEquals(2, timer.count());

		double totalTimeSeconds = timer.totalTime(TimeUnit.NANOSECONDS) / 1_000_000_000.0;
		assertEquals(0.3, totalTimeSeconds, 0.01);
	}

	@Test
	void multipleMetricOperations_ShouldTrackIndependently() {
		jmsMetrics.recordMessageSent();
		jmsMetrics.recordMessageReceived();
		jmsMetrics.recordMessageReceived();
		jmsMetrics.recordProcessingTime(150);
		jmsMetrics.recordMessageFailed();
		jmsMetrics.recordMessageSent();

		assertEquals(2.0, Objects.requireNonNull(registry.find("jms.messages.sent").counter()).count());
		assertEquals(2.0, Objects.requireNonNull(registry.find("jms.messages.received").counter()).count());
		assertEquals(1.0, Objects.requireNonNull(registry.find("jms.messages.failed").counter()).count());
		assertEquals(1, Objects.requireNonNull(registry.find("jms.messages.processing.time").timer()).count());
	}
}
