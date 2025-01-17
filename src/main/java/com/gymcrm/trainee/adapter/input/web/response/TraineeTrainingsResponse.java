package com.gymcrm.trainee.adapter.input.web.response;

import com.gymcrm.training.domain.Training;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeTrainingsResponse {
  private String trainingName;
  private String trainingDate;
  private String trainingType;
  private int trainingDuration;
  private String trainerName;

  public static TraineeTrainingsResponse from(Training training) {
    return new TraineeTrainingsResponse(
        training.getTrainingName(),
        training.getTrainingDate().toString(),
        training.getTrainingType().getTrainingTypeName(),
        training.getTrainingDuration(),
        training.getTrainer().getUser().getUsername());
  }

  public static List<TraineeTrainingsResponse> from(List<Training> training) {
    return training == null
        ? Collections.emptyList()
        : training.stream().map(TraineeTrainingsResponse::from).collect(Collectors.toList());
  }
}
