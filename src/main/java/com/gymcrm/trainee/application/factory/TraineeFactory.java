package com.gymcrm.trainee.application.factory;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import com.gymcrm.trainee.domain.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeFactory {
  private final UUIDGeneratorInterface uuidGenerator;

  public TraineeFactory(UUIDGeneratorInterface uuidGenerator) {
    this.uuidGenerator = uuidGenerator;
  }

  public Trainee createFrom(CreateTraineeCommand command) {
    return new Trainee(
        uuidGenerator.newUUID(), command.getDateOfBirth(), command.getAddress(), command.getUser());
  }
}
