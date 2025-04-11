package com.gymcrm.training.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.training.application.port.input.UpdateTrainingCommand;
import java.time.LocalDate;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingUpdateRequest {
	@Size(min = 1,max = 200)
	@JsonProperty("trainee_username")
	String traineeUsername;

	@Size(min = 1,max = 200)
	@JsonProperty("trainer_username")
	String trainerUsername;

	@Size(min = 1,max = 200)
	@JsonProperty("training_name")
	String trainingName;

	@JsonProperty("training_date")
	LocalDate trainingDate;

	@JsonProperty("training_duration")
	Integer trainingDuration;

	public UpdateTrainingCommand toCommand() {
		return UpdateTrainingCommand.builder().traineeUsername(traineeUsername).trainerUsername(trainerUsername)
		        .trainingName(trainingName).trainingDate(trainingDate).trainingDuration(trainingDuration).build();
	}
}
