package com.gymcrm.command;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateTrainerCommand {
  UUID trainerId;
  String specialization;
}
