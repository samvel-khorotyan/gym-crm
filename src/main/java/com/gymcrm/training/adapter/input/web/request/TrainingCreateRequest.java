package com.gymcrm.training.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.training.application.port.input.CreateTrainingCommand;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainingCreateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("trainee_username")
	String traineeUsername;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("trainer_username")
	String trainerUsername;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("training_name")
	String trainingName;

	@NotNull
	@JsonProperty("training_date")
	LocalDate trainingDate;

	@NotNull
	@JsonProperty("training_duration")
	Integer trainingDuration;

	public CreateTrainingCommand toCommand() {
		return new CreateTrainingCommand(traineeUsername, trainerUsername, trainingName, trainingDate,
		        trainingDuration);
	}
}
