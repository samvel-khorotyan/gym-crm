package com.gymcrm.usecase;

import com.gymcrm.command.CreateTrainerCommand;

public interface TrainerCreationUseCase {
  void createTrainer(CreateTrainerCommand command);
}
