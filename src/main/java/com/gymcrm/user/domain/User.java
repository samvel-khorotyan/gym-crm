package com.gymcrm.user.domain;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.util.UUIDCharType;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.Type;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
  @Id
  @Type(value = UUIDCharType.class)
  private UUID id;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type", nullable = false)
  private UserType userType;

  @OneToOne(mappedBy = "user")
  private Trainee trainee;

  @OneToOne(mappedBy = "user")
  private Trainer trainer;

  public User(
      UUID id,
      String firstName,
      String lastName,
      String username,
      String password,
      Boolean isActive,
      UserType userType) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.password = password;
    this.isActive = isActive;
    this.userType = userType;
  }
}
