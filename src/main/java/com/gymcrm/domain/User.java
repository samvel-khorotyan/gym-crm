package com.gymcrm.domain;

import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User implements CRMEntity {
  private UUID id;
  private String firstName;
  private String lastName;
  private String username;
  private String password;
  private boolean isActive;

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }
}
