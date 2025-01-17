package com.gymcrm.training.application.port.input;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTrainingCommand {
  String traineeUsername;
  String trainerUsername;
  Trainee trainee;
  Trainer trainer;
  String trainingName;
  TrainingType trainingType;
  LocalDate trainingDate;
  Integer trainingDuration;

  public CreateTrainingCommand(
      Trainee trainee,
      Trainer trainer,
      String trainingName,
      TrainingType trainingType,
      LocalDate trainingDate,
      Integer trainingDuration) {
    this.trainee = trainee;
    this.trainer = trainer;
    this.trainingName = trainingName;
    this.trainingType = trainingType;
    this.trainingDate = trainingDate;
    this.trainingDuration = trainingDuration;
  }

  public CreateTrainingCommand(
      String traineeUsername,
      String trainerUsername,
      String trainingName,
      LocalDate trainingDate,
      Integer trainingDuration) {
    this.traineeUsername = traineeUsername;
    this.trainerUsername = trainerUsername;
    this.trainingName = trainingName;
    this.trainingDate = trainingDate;
    this.trainingDuration = trainingDuration;
  }
}
