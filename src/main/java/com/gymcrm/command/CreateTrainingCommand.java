package com.gymcrm.command;

import com.gymcrm.domain.TrainingType;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateTrainingCommand {
  UUID traineeId;
  UUID trainerId;
  String trainingName;
  TrainingType trainingType;
  LocalDate trainingDate;
  Integer trainingDuration;
}
