package com.gymcrm.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trainee implements CRMEntity {
  private UUID id;
  private LocalDate dateOfBirth;
  private String address;
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
