package com.gymcrm.configuration.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadMessage;
import com.gymcrm.trainer.adapter.output.queue.message.TrainerWorkloadResponseMessage;
import java.util.HashMap;
import java.util.Map;
import javax.jms.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.util.ErrorHandler;

@EnableJms
@Configuration
public class JmsConfig {
	private static final Logger log = LoggerFactory.getLogger(JmsConfig.class);

	public static final String TRAINER_WORKLOAD_QUEUE = "trainer-workload-queue";
	public static final String TRAINER_WORKLOAD_RESPONSE_QUEUE = "trainer-workload-response-queue";
	public static final String DEAD_LETTER_QUEUE = "dead-letter-queue";

	@Bean
	public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
	        @Qualifier("cachingConnectionFactory") ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setMessageConverter(jacksonJmsMessageConverter());
		factory.setErrorHandler(jmsErrorHandler());
		factory.setConcurrency("3-10");
		factory.setSessionTransacted(true);
		return factory;
	}

	@Bean
	public ErrorHandler jmsErrorHandler() {
		return t -> log.error("Error in JMS listener: {}", t.getMessage(), t);
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		converter.setObjectMapper(objectMapper);

		converter.setTypeIdPropertyName("_class");
		Map<String, Class<?>> typeIdMappings = new HashMap<>();
		typeIdMappings.put("TrainerWorkloadMessage", TrainerWorkloadMessage.class);
		typeIdMappings.put("TrainerWorkloadResponseMessage", TrainerWorkloadResponseMessage.class);
		converter.setTypeIdMappings(typeIdMappings);

		return converter;
	}

	@Bean
	public JmsTemplate jmsTemplate(@Qualifier("cachingConnectionFactory") ConnectionFactory connectionFactory) {
		JmsTemplate template = new JmsTemplate(connectionFactory);
		template.setMessageConverter(jacksonJmsMessageConverter());
		template.setDeliveryPersistent(true);
		template.setExplicitQosEnabled(true);
		template.setTimeToLive(60000);
		return template;
	}
}
