package com.gymcrm.trainee.application.port.input;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTraineeCommand {
  UUID traineeId;
  LocalDate dateOfBirth;
  String address;
  Training training;

  public UpdateTraineeCommand(Training training) {
    this.training = training;
  }
}
