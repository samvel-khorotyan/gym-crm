package com.gymcrm.domain;

import java.util.UUID;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements CRMEntity {
  private UUID id;
  private String firstName;
  private String lastName;
  private String username;
  private String password;
  private Boolean isActive;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }
}
