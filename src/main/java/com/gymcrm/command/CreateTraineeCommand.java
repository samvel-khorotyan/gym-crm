package com.gymcrm.command;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateTraineeCommand {
  String address;
  LocalDate dateOfBirth;
  String userFirstName;
  String userLastName;
  UUID userId;
  String username;
  String password;
  Boolean isActive;
}
