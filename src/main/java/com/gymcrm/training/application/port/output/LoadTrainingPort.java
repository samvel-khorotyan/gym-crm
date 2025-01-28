package com.gymcrm.training.application.port.output;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LoadTrainingPort {
  Training findById(UUID id);

  List<Training> findAll();

  List<Training> findTraineeTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String trainerName, String trainingType);

  List<Training> findTrainerTrainingsByCriteria(
      LocalDate startDate, LocalDate endDate, String traineeName);
}
