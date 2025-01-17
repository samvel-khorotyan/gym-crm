package com.gymcrm.trainee.adapter.input.web.response;

import com.gymcrm.trainee.domain.Trainee;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeDetailsResponse {
  private String username;
  private String firstName;
  private String lastName;

  public static TraineeDetailsResponse from(Trainee trainee) {
    return new TraineeDetailsResponse(
        trainee.getUser().getUsername(),
        trainee.getUser().getFirstName(),
        trainee.getUser().getLastName());
  }

  public static List<TraineeDetailsResponse> from(List<Trainee> trainees) {
    return trainees == null
        ? Collections.emptyList()
        : trainees.stream().map(TraineeDetailsResponse::from).collect(Collectors.toList());
  }
}
