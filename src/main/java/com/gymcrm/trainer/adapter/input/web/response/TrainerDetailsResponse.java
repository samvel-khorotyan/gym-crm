package com.gymcrm.trainer.adapter.input.web.response;

import com.gymcrm.trainer.domain.Trainer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainerDetailsResponse {
  private String username;
  private String firstName;
  private String lastName;
  private String specialization;

  public static TrainerDetailsResponse from(Trainer trainer) {
    return new TrainerDetailsResponse(
        trainer.getUser().getUsername(),
        trainer.getUser().getFirstName(),
        trainer.getUser().getLastName(),
        trainer.getSpecialization());
  }

  public static List<TrainerDetailsResponse> from(List<Trainer> trainees) {
    return trainees == null
        ? Collections.emptyList()
        : trainees.stream().map(TrainerDetailsResponse::from).collect(Collectors.toList());
  }
}
