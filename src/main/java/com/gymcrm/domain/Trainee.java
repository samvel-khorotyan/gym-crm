package com.gymcrm.domain;

import java.time.LocalDate;
import java.util.UUID;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Trainee extends User {
  private UUID id;
  private LocalDate dateOfBirth;
  private String address;
  private UUID userId;

  public Trainee(UUID userId, String firstName, String lastName) {
    super(firstName, lastName);
    this.userId = userId;
  }

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
    return "Trainee{"
        + "id="
        + id
        + ", dateOfBirth="
        + dateOfBirth
        + ", address='"
        + address
        + '\''
        + ", userId="
        + userId
        + '\''
        + ", firstName="
        + firstName
        + '\''
        + ", lastName="
        + lastName
        + '\''
        + ", username="
        + username
        + '\''
        + ", password="
        + password
        + '\''
        + ", isActive="
        + isActive
        + '\''
        + '}';
  }
}
