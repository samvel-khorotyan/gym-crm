package com.gymcrm.training.domain;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.trainingtype.domain.TrainingType;
import com.gymcrm.util.UUIDCharType;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.Type;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "training")
public class Training {
  @Id
  @Type(value = UUIDCharType.class)
  private UUID id;

  @Column(name = "training_name ", nullable = false)
  private String trainingName;

  @ManyToOne
  @JoinColumn(name = "trainee_id", nullable = false)
  private Trainee trainee;

  @ManyToOne
  @JoinColumn(name = "trainer_id", nullable = false)
  private Trainer trainer;

  @ManyToOne
  @JoinColumn(name = "training_type_id", nullable = false)
  private TrainingType trainingType;

  @Column(name = "training_date", nullable = false)
  private LocalDate trainingDate;

  @Column(name = "training_duration", nullable = false)
  private Integer trainingDuration;

  @Override
  public String toString() {
    return "Training{"
        + "id="
        + id
        + ", trainingName='"
        + trainingName
        + '\''
        + ", trainingDate='"
        + trainingDate
        + '\''
        + ", trainingDuration='"
        + trainingDuration
        + '\''
        + ",\ntrainee="
        + trainee
        + ",\ntrainer="
        + trainer
        + ",\ntrainingType="
        + trainingType
        + '}';
  }
}
