package com.gymcrm.training.application.port.output;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.List;

public interface LoadTrainingPort {
  List<Training> findAll();

  List<Training> findAllByTrainerUsernames(List<String> usernames);

  List<Training> findTraineeTrainingsByCriteria(
      String username,
      LocalDate startDate,
      LocalDate endDate,
      String trainerName,
      String trainingType);

  List<Training> findTrainerTrainingsByCriteria(
      String username, LocalDate startDate, LocalDate endDate, String traineeName);
}
