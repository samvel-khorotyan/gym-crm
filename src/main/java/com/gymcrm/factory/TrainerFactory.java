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
    return new Trainer(
        uuidGeneratorInterface.newUUID(), command.getSpecialization(), command.getUserId());
  }
}
