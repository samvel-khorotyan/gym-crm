package com.gymcrm.configuration.messaging;

import com.gymcrm.configuration.messaging.metrics.JmsMetrics;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterQueueListener {
	private final JmsMetrics jmsMetrics;

	@JmsListener(destination = JmsConfig.DEAD_LETTER_QUEUE)
	public void handleDeadLetterMessage(Message message) {
		try {
			jmsMetrics.recordMessageReceived();
			jmsMetrics.recordMessageFailed();

			if (message instanceof TextMessage textMessage) {
				String messageText = textMessage.getText();
				log.error("Received message in Dead Letter Queue: {}", messageText);

				processDeadLetterMessage(message);
			} else {
				log.error("Received non-text message in Dead Letter Queue of type: {}", message.getClass().getName());
			}
		} catch (JMSException e) {
			log.error("Error processing message from Dead Letter Queue", e);
		}
	}

	private void processDeadLetterMessage(Message message) {
		try {
			String messageId = message.getJMSMessageID();
			String correlationId = message.getJMSCorrelationID();

			log.info("Dead Letter Message details - ID: {}, Correlation ID: {}", messageId, correlationId);
		} catch (JMSException e) {
			log.error("Error extracting message details from Dead Letter Queue", e);
		}
	}
}
