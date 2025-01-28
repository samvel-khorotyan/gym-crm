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
  String trainingName;
  Trainee trainee;
  Trainer trainer;
  TrainingType trainingType;
  LocalDate trainingDate;
  Integer trainingDuration;
}
