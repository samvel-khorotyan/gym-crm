package com.gymcrm.service;

import com.gymcrm.command.UpdateTrainerCommand;

public interface TrainerUpdateUseCase {
  void updateTrainer(UpdateTrainerCommand command);
}
