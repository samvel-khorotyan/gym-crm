package com.gymcrm.trainee.application.port.input;

import com.gymcrm.user.domain.User;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTraineeCommand {
  LocalDate dateOfBirth;
  String address;
  User user;

  public CreateTraineeCommand(LocalDate dateOfBirth, String address) {
    this.dateOfBirth = dateOfBirth;
    this.address = address;
  }
}
