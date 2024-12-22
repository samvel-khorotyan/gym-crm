package com.gymcrm.trainingtype.application;

import com.gymcrm.trainingtype.application.exception.TrainingTypeNotFoundException;
import com.gymcrm.trainingtype.application.factory.TrainingTypeFactory;
import com.gymcrm.trainingtype.application.port.input.CreateTrainingTypeCommand;
import com.gymcrm.trainingtype.application.port.input.TrainingTypeCreationUseCase;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.application.port.output.UpdateTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingTypeService implements TrainingTypeCreationUseCase {
  private static final Logger logger = LoggerFactory.getLogger(TrainingTypeService.class);

  private final TrainingTypeFactory trainingTypeFactory;
  private final UpdateTrainingTypePort updateTrainingTypePort;
  private final LoadTrainingTypePort loadTrainingTypePort;

  @Autowired
  public TrainingTypeService(
      TrainingTypeFactory trainingTypeFactory,
      UpdateTrainingTypePort updateTrainingTypePort,
      LoadTrainingTypePort loadTrainingTypePort) {
    this.trainingTypeFactory = trainingTypeFactory;
    this.updateTrainingTypePort = updateTrainingTypePort;
    this.loadTrainingTypePort = loadTrainingTypePort;
  }

  @Override
  public TrainingType create(CreateTrainingTypeCommand command) {
    logger.debug("Validating training type name creation command");
    if (command.getTrainingTypeName() == null || command.getTrainingTypeName().isBlank()) {
      throw new IllegalArgumentException("Training type name cannot be null or empty");
    }

    try {
      return loadTrainingTypePort.findByTrainingTypeName(command.getTrainingTypeName());
    } catch (TrainingTypeNotFoundException e) {
      return updateTrainingTypePort.save(trainingTypeFactory.createFrom(command));
    }
  }
}
