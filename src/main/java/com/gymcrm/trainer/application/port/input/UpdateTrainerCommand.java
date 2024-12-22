package com.gymcrm.trainer.application.port.input;

import java.util.UUID;
import lombok.Data;

@Data
public class UpdateTrainerCommand {
  UUID trainerId;
  String specialization;
}
