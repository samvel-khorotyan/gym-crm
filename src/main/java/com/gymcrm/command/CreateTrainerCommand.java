package com.gymcrm.command;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateTrainerCommand {
  String specialization;
  String userFirstName;
  String userLastName;
  UUID userId;
  String username;
  String password;
  Boolean isActive;
}
