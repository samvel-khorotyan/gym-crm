package com.gymcrm.trainer.domain;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.user.domain.User;
import com.gymcrm.util.UUIDCharType;
import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.Type;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainer")
public class Trainer {
  @Id
  @Type(value = UUIDCharType.class)
  private UUID id;

  @Column(nullable = false)
  private String specialization;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToMany(mappedBy = "trainers")
  private List<Trainee> trainees;

  public Trainer(UUID id, String specialization, User user) {
    this.id = id;
    this.specialization = specialization;
    this.user = user;
  }

  @Override
  public String toString() {
    return "Trainer{"
        + "id="
        + id
        + ", specialization='"
        + specialization
        + '\''
        + ", userId='"
        + user.getId()
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
