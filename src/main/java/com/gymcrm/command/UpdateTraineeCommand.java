package com.gymcrm.command;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateTraineeCommand {
  UUID traineeId;
  LocalDate dateOfBirth;
  String address;
}
