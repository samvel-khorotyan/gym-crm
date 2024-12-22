package com.gymcrm.trainingtype.domain;

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
@Table(name = "training_type")
public class TrainingType {
  @Id
  @Type(value = UUIDCharType.class)
  private UUID id;

  @Column(name = "training_type_name ", nullable = false, unique = true)
  private String trainingTypeName;
}
