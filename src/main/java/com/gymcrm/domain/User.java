package com.gymcrm.domain;

import java.util.UUID;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements CRMEntity {
  private UUID id;
  String firstName;
  String lastName;
  String username;
  String password;
  Boolean isActive;

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
