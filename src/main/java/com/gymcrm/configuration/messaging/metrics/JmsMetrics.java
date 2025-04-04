package com.gymcrm.configuration.messaging.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JmsMetrics {
	private final Counter messagesSentCounter;
	private final Counter messagesReceivedCounter;
	private final Counter messagesFailedCounter;
	private final Timer messageProcessingTimer;

	public JmsMetrics(MeterRegistry registry) {
		this.messagesSentCounter = Counter.builder("jms.messages.sent").description("Number of JMS messages sent")
		        .register(registry);

		this.messagesReceivedCounter = Counter.builder("jms.messages.received")
		        .description("Number of JMS messages received").register(registry);

		this.messagesFailedCounter = Counter.builder("jms.messages.failed")
		        .description("Number of JMS messages that failed to process").register(registry);

		this.messageProcessingTimer = Timer.builder("jms.messages.processing.time")
		        .description("Time taken to process JMS messages").register(registry);
	}

	public void recordMessageSent() {
		messagesSentCounter.increment();
	}

	public void recordMessageReceived() {
		messagesReceivedCounter.increment();
	}

	public void recordMessageFailed() {
		messagesFailedCounter.increment();
	}

	public void recordProcessingTime(long timeInMs) {
		messageProcessingTimer.record(timeInMs, TimeUnit.MILLISECONDS);
	}
}
