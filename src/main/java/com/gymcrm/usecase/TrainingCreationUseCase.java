package com.gymcrm.usecase;

import com.gymcrm.command.CreateTrainingCommand;

public interface TrainingCreationUseCase {
  void createTraining(CreateTrainingCommand command);
}
