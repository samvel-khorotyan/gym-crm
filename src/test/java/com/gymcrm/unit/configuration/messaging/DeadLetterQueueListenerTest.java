package com.gymcrm.unit.configuration.messaging;

import static org.mockito.Mockito.*;

import com.gymcrm.configuration.messaging.DeadLetterQueueListener;
import com.gymcrm.configuration.messaging.JmsConfig;
import com.gymcrm.configuration.messaging.metrics.JmsMetrics;
import java.lang.reflect.Method;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeadLetterQueueListenerTest {
	@Mock
	private JmsMetrics jmsMetrics;

	@Mock
	private TextMessage textMessage;

	@Mock
	private Message nonTextMessage;

	@InjectMocks
	private DeadLetterQueueListener deadLetterQueueListener;

	@Test
  void handleDeadLetterMessage_WithTextMessage_ShouldProcessCorrectly() throws JMSException {
    when(textMessage.getText()).thenReturn("Test message content");
    when(textMessage.getJMSMessageID()).thenReturn("test-message-id");
    when(textMessage.getJMSCorrelationID()).thenReturn("test-correlation-id");

    deadLetterQueueListener.handleDeadLetterMessage(textMessage);

    verify(jmsMetrics).recordMessageReceived();
    verify(jmsMetrics).recordMessageFailed();
    verify(textMessage).getText();
    verify(textMessage).getJMSMessageID();
    verify(textMessage).getJMSCorrelationID();
  }

	@Test
	void handleDeadLetterMessage_WithNonTextMessage_ShouldLogTypeAndNotProcess() {
		deadLetterQueueListener.handleDeadLetterMessage(nonTextMessage);

		verify(jmsMetrics).recordMessageReceived();
		verify(jmsMetrics).recordMessageFailed();
	}

	@Test
  void handleDeadLetterMessage_WithTextMessageThrowingException_ShouldHandleGracefully()
      throws JMSException {
    when(textMessage.getText()).thenThrow(new JMSException("Error getting text"));

    deadLetterQueueListener.handleDeadLetterMessage(textMessage);

    verify(jmsMetrics).recordMessageReceived();
    verify(jmsMetrics).recordMessageFailed();
    verify(textMessage).getText();
    verifyNoMoreInteractions(textMessage);
  }

	@Test
  void processDeadLetterMessage_WithMessageThrowingException_ShouldHandleGracefully()
      throws JMSException {
    when(textMessage.getText()).thenReturn("Test message content");
    when(textMessage.getJMSMessageID()).thenThrow(new JMSException("Error getting message ID"));

    deadLetterQueueListener.handleDeadLetterMessage(textMessage);

    verify(jmsMetrics).recordMessageReceived();
    verify(jmsMetrics).recordMessageFailed();
    verify(textMessage).getText();
    verify(textMessage).getJMSMessageID();
    verifyNoMoreInteractions(textMessage);
  }

	@Test
  void processDeadLetterMessage_WithCorrelationIdThrowingException_ShouldHandleGracefully()
      throws JMSException {
    when(textMessage.getText()).thenReturn("Test message content");
    when(textMessage.getJMSMessageID()).thenReturn("test-message-id");
    when(textMessage.getJMSCorrelationID())
        .thenThrow(new JMSException("Error getting correlation ID"));

    deadLetterQueueListener.handleDeadLetterMessage(textMessage);

    verify(jmsMetrics).recordMessageReceived();
    verify(jmsMetrics).recordMessageFailed();
    verify(textMessage).getText();
    verify(textMessage).getJMSMessageID();
    verify(textMessage).getJMSCorrelationID();
  }

	@Test
	void verifyJmsListenerAnnotation() throws NoSuchMethodException {
		Method method = DeadLetterQueueListener.class.getMethod("handleDeadLetterMessage", Message.class);

		org.springframework.jms.annotation.JmsListener annotation = method
		        .getAnnotation(org.springframework.jms.annotation.JmsListener.class);

		org.junit.jupiter.api.Assertions.assertNotNull(annotation);
		org.junit.jupiter.api.Assertions.assertEquals(JmsConfig.DEAD_LETTER_QUEUE, annotation.destination());
	}
}
