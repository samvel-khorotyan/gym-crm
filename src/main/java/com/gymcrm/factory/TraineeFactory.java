package com.gymcrm.factory;

import com.gymcrm.command.CreateTraineeCommand;
import com.gymcrm.domain.Trainee;
import com.gymcrm.util.UUIDGeneratorInterface;
import org.springframework.stereotype.Component;

@Component
public class TraineeFactory {
  private final UUIDGeneratorInterface uuidGeneratorInterface;

  public TraineeFactory(UUIDGeneratorInterface uuidGeneratorInterface) {
    this.uuidGeneratorInterface = uuidGeneratorInterface;
  }

  public Trainee createFrom(CreateTraineeCommand command) {
    Trainee trainee =
        new Trainee(
            uuidGeneratorInterface.newUUID(),
            command.getDateOfBirth(),
            command.getAddress(),
            command.getUserId());

    trainee.setFirstName(command.getUserFirstName());
    trainee.setLastName(command.getUserLastName());
    trainee.setUsername(command.getUsername());
    trainee.setPassword(command.getPassword());
    trainee.setIsActive(command.getIsActive());

    return trainee;
  }
}
