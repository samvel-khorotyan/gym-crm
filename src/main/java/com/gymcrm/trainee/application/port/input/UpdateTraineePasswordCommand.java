package com.gymcrm.trainee.application.port.input;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTraineePasswordCommand {
  private UUID traineeId;
  private String username;
  private String oldPassword;
  private String newPassword;
}
