package com.gymcrm.factory;

import com.gymcrm.command.CreateTrainerCommand;
import com.gymcrm.domain.Trainer;
import com.gymcrm.util.UUIDGeneratorInterface;
import org.springframework.stereotype.Component;

@Component
public class TrainerFactory {
  private final UUIDGeneratorInterface uuidGeneratorInterface;

  public TrainerFactory(UUIDGeneratorInterface uuidGeneratorInterface) {
    this.uuidGeneratorInterface = uuidGeneratorInterface;
  }

  public Trainer createFrom(CreateTrainerCommand command) {
    Trainer trainer =
        new Trainer(
            uuidGeneratorInterface.newUUID(), command.getSpecialization(), command.getUserId());

    trainer.setFirstName(command.getUserFirstName());
    trainer.setLastName(command.getUserLastName());
    trainer.setUsername(command.getUsername());
    trainer.setPassword(command.getPassword());
    trainer.setIsActive(command.getIsActive());

    return trainer;
  }
}
