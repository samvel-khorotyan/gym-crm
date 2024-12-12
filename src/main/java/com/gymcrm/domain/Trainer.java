package com.gymcrm.domain;

import java.util.UUID;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Trainer extends User {
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

  @Override
  public String toString() {
    return "Trainer{"
        + "id="
        + id
        + ", specialization="
        + specialization
        + ", userId='"
        + userId
        + '\''
        + ", firstName="
        + getFirstName()
        + '\''
        + ", lastName="
        + getLastName()
        + '\''
        + ", username="
        + getUsername()
        + '\''
        + ", password="
        + getPassword()
        + '\''
        + ", isActive="
        + getIsActive()
        + '\''
        + '}';
  }
}
