package com.gymcrm.trainer.application.port.input;

import com.gymcrm.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTrainerCommand {
  String specialization;
  User user;

  public CreateTrainerCommand(String specialization) {
    this.specialization = specialization;
  }
}
