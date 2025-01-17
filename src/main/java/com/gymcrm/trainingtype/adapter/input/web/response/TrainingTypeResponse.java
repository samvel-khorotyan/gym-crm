package com.gymcrm.trainingtype.adapter.input.web.response;

import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainingTypeResponse {
  private String trainingTypeId;
  private String trainingType;

  public static TrainingTypeResponse from(TrainingType trainingType) {
    return new TrainingTypeResponse(
        trainingType.getId().toString(), trainingType.getTrainingTypeName());
  }

  public static List<TrainingTypeResponse> from(List<TrainingType> trainees) {
    return trainees == null
        ? Collections.emptyList()
        : trainees.stream().map(TrainingTypeResponse::from).collect(Collectors.toList());
  }
}
