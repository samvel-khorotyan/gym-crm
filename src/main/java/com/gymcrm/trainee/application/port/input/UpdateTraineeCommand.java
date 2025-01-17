package com.gymcrm.trainee.application.port.input;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTraineeCommand {
  UUID traineeId;
  String firstName;
  String lastName;
  LocalDate dateOfBirth;
  String address;
  Training training;
  Boolean isActive;

  public UpdateTraineeCommand(
      UUID traineeId,
      String firstName,
      String lastName,
      LocalDate dateOfBirth,
      String address,
      Boolean isActive) {
    this.traineeId = traineeId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
    this.isActive = isActive;
  }
}
