package com.gymcrm.training.application.port.input;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoadTrainingUseCase {
  Training loadById(UUID trainingId);

  List<Training> loadAll();

  List<Training> findTraineeTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String trainerName, String trainingType);

  List<Training> findTrainerTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String traineeName);
}
