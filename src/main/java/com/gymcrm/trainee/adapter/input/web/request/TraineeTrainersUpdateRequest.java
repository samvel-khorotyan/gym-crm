package com.gymcrm.trainee.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.trainee.application.port.input.UpdateTraineeTrainersCommand;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TraineeTrainersUpdateRequest {
  @NotBlank
  @Size(min = 1, max = 200)
  @JsonProperty("trainee_username")
  private String traineeUsername;

  @NotEmpty
  @Size(min = 1, max = 200)
  @JsonProperty("trainer_usernames")
  private List<String> trainerUsernames;

  public UpdateTraineeTrainersCommand toCommand() {
    return new UpdateTraineeTrainersCommand(traineeUsername, trainerUsernames);
  }
}
