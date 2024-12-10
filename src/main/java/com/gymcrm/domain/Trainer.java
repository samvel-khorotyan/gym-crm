package com.gymcrm.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainer implements CRMEntity {
  private UUID id;
  private String specialization;
  private UUID userId;

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }
}
