package com.gymcrm.service;

import com.gymcrm.command.CreateTrainingCommand;

public interface TrainingCreationUseCase {
  void createTraining(CreateTrainingCommand command);
}
