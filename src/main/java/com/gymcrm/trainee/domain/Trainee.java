package com.gymcrm.trainee.domain;

import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.training.domain.Training;
import com.gymcrm.user.domain.User;
import com.gymcrm.util.UUIDCharType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainee")
public class Trainee {
  @Id
  @Type(value = UUIDCharType.class)
  private UUID id;

  @Column(name = "date_of_birth ")
  private LocalDate dateOfBirth;

  private String address;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(
      mappedBy = "trainee",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private List<Training> trainings;

  @ManyToMany
  @JoinTable(
      name = "trainee_trainer",
      joinColumns = @JoinColumn(name = "trainee_id"),
      inverseJoinColumns = @JoinColumn(name = "trainer_id"))
  private List<Trainer> trainers;

  public Trainee(UUID id, LocalDate dateOfBirth, String address, User user) {
    this.id = id;
    this.dateOfBirth = dateOfBirth;
    this.address = address;
    this.user = user;
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
        + ", firstName='"
        + user.getFirstName()
        + '\''
        + ", lastName='"
        + user.getLastName()
        + '\''
        + ", username='"
        + user.getUsername()
        + '\''
        + ", password='"
        + user.getPassword()
        + '\''
        + ", isActive="
        + user.getIsActive()
        + '}';
  }
}
