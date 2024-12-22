package com.gymcrm.trainingtype.application.factory;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainingtype.application.port.input.CreateTrainingTypeCommand;
import com.gymcrm.trainingtype.domain.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeFactory {
  private final UUIDGeneratorInterface uuidGenerator;

  public TrainingTypeFactory(UUIDGeneratorInterface uuidGenerator) {
    this.uuidGenerator = uuidGenerator;
  }

  public TrainingType createFrom(CreateTrainingTypeCommand command) {
    return new TrainingType(uuidGenerator.newUUID(), command.getTrainingTypeName());
  }
}
