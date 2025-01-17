package com.gymcrm.trainee.application.port.input;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTraineeTrainersCommand {
  private String traineeUsername;
  private List<String> trainerUsernames;
}
