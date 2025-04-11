package com.gymcrm.training.adapter.input.web.response;

import com.gymcrm.training.domain.Training;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TrainingResponse {
	private UUID id;
	private String trainingName;
	private String traineeUsername;
	private String trainerUsername;
	private String trainingType;
	private LocalDate trainingDate;
	private Integer trainingDuration;

	public static TrainingResponse from(Training training) {
		return TrainingResponse.builder().id(training.getId()).trainingName(training.getTrainingName())
		        .traineeUsername(training.getTrainee().getUser().getUsername())
		        .trainerUsername(training.getTrainer().getUser().getUsername())
		        .trainingType(training.getTrainingType().getTrainingTypeName()).trainingDate(training.getTrainingDate())
		        .trainingDuration(training.getTrainingDuration()).build();
	}
}
