package com.gymcrm.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Training implements CRMEntity {
  private UUID id;
  private UUID traineeId;
  private UUID trainerId;
  private String trainingName;
  private TrainingType trainingType;
  private LocalDate trainingDate;
  private Integer trainingDuration;

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }
}
