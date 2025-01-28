package com.gymcrm.trainingtype.application;

import com.gymcrm.trainingtype.application.port.input.LoadTrainingTypeUseCase;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class TrainingTypeService implements LoadTrainingTypeUseCase {
	private static final Logger logger = LoggerFactory.getLogger(TrainingTypeService.class);

	private final LoadTrainingTypePort loadTrainingTypePort;

	public TrainingTypeService(LoadTrainingTypePort loadTrainingTypePort) {
		this.loadTrainingTypePort = loadTrainingTypePort;
	}

	@Override
	public List<TrainingType> loadAll() {
		String transactionId = MDC.get("transactionId");

		logger.info("Transaction ID: {} - Fetching all training types.", transactionId);

		try {
			List<TrainingType> trainingTypes = loadTrainingTypePort.findAll();
			logger.info("Transaction ID: {} - Successfully fetched {} training types.", transactionId,
			        trainingTypes.size());
			return trainingTypes;
		} catch (Exception e) {
			logger.error("Transaction ID: {} - Failed to fetch training types, Reason: {}", transactionId,
			        e.getMessage(), e);
			throw new RuntimeException("Failed to fetch training types.", e);
		}
	}
}
