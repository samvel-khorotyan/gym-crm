package com.gymcrm.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Training implements CRMEntity {
  private UUID id;
  private UUID traineeId;
  private UUID trainerId;
  private String trainingName;
  private TrainingType trainingType;
  private LocalDate trainingDate;
  private int trainingDuration;

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }
}
