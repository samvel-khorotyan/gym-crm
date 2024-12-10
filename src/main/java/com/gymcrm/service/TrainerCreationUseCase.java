package com.gymcrm.service;

import com.gymcrm.command.CreateTrainerCommand;

public interface TrainerCreationUseCase {
  void createTrainer(CreateTrainerCommand command);
}
