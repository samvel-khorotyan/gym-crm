package com.gymcrm.trainingtype.domain;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
  @Type(type = "uuid-char")
  private UUID id;

  @Column(name = "training_type_name ", nullable = false, unique = true)
  private String trainingTypeName;
}
