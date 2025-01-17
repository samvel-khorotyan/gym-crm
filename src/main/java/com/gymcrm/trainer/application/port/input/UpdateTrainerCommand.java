package com.gymcrm.trainer.application.port.input;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTrainerCommand {
  UUID trainerId;
  String firstName;
  String lastName;
  String specialization;
  Boolean isActive;
}
