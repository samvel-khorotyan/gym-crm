package com.gymcrm.training.application.factory;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import com.gymcrm.training.domain.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingFactory {
  private final UUIDGeneratorInterface uuidGenerator;

  public TrainingFactory(UUIDGeneratorInterface uuidGenerator) {
    this.uuidGenerator = uuidGenerator;
  }

  public Training createFrom(CreateTrainingCommand command) {
    return new Training(
        uuidGenerator.newUUID(),
        command.getTrainingName(),
        command.getTrainee(),
        command.getTrainer(),
        command.getTrainingType(),
        command.getTrainingDate(),
        command.getTrainingDuration());
  }
}
