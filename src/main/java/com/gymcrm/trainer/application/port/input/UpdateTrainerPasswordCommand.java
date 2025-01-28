package com.gymcrm.trainer.application.port.input;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTrainerPasswordCommand {
  private UUID trainerId;
  private String username;
  private String oldPassword;
  private String newPassword;
}
