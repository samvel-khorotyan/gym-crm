package com.gymcrm.factory;

import com.gymcrm.command.CreateTrainingCommand;
import com.gymcrm.domain.Training;
import com.gymcrm.util.UUIDGeneratorInterface;
import org.springframework.stereotype.Component;

@Component
public class TrainingFactory {
  private final UUIDGeneratorInterface uuidGeneratorInterface;

  public TrainingFactory(UUIDGeneratorInterface uuidGeneratorInterface) {
    this.uuidGeneratorInterface = uuidGeneratorInterface;
  }

  public Training createFrom(CreateTrainingCommand command) {
    return new Training(
        uuidGeneratorInterface.newUUID(),
        command.getTraineeId(),
        command.getTrainerId(),
        command.getTrainingName(),
        command.getTrainingType(),
        command.getTrainingDate(),
        command.getTrainingDuration());
  }
}
