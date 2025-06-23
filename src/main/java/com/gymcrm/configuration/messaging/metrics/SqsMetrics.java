package com.gymcrm.configuration.messaging.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SqsMetrics {
    private final Counter messagesSentCounter;
    private final Counter messagesReceivedCounter;
    private final Counter messagesFailedCounter;
    private final Timer messageProcessingTimer;

    public SqsMetrics(MeterRegistry registry) {
        this.messagesSentCounter = Counter.builder("sqs.messages.sent")
                .description("Number of SQS messages sent")
                .register(registry);

        this.messagesReceivedCounter = Counter.builder("sqs.messages.received")
                .description("Number of SQS messages received")
                .register(registry);

        this.messagesFailedCounter = Counter.builder("sqs.messages.failed")
                .description("Number of SQS messages that failed to process")
                .register(registry);

        this.messageProcessingTimer = Timer.builder("sqs.messages.processing.time")
                .description("Time taken to process SQS messages")
                .register(registry);
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
