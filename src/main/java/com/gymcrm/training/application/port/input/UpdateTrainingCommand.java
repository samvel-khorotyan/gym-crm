package com.gymcrm.training.application.port.input;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTrainingCommand {
	private String traineeUsername;
	private String trainerUsername;
	private String trainingName;
	private LocalDate trainingDate;
	private Integer trainingDuration;
	private Trainee trainee;
	private Trainer trainer;
	private TrainingType trainingType;
}
