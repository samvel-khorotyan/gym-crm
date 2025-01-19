package com.gymcrm.trainer.adapter.input.web.response;

import com.gymcrm.trainee.adapter.input.web.response.TraineeDetailsResponse;
import com.gymcrm.trainer.domain.Trainer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerResponse extends RepresentationModel<TrainerUserDetailsResponse> {
  private String firstName;
  private String lastName;
  private String specialization;
  private boolean isActive;
  private List<TraineeDetailsResponse> traineesList;

  public static TrainerResponse from(Trainer trainer) {
    return new TrainerResponse(
        trainer.getUser().getFirstName(),
        trainer.getUser().getLastName(),
        trainer.getSpecialization(),
        trainer.getUser().getIsActive(),
        TraineeDetailsResponse.from(trainer.getTrainees()));
  }

  public static List<TrainerResponse> from(List<Trainer> trainees) {
    return trainees == null
        ? Collections.emptyList()
        : trainees.stream().map(TrainerResponse::from).collect(Collectors.toList());
  }
}
