package com.gymcrm.trainer.adapter.input.web.response;

import com.gymcrm.trainer.domain.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerLightResponse {
  private String firstName;
  private String lastName;

  public static TrainerLightResponse from(Trainer trainer) {
    return new TrainerLightResponse(
        trainer.getUser().getFirstName(), trainer.getUser().getLastName());
  }
}
