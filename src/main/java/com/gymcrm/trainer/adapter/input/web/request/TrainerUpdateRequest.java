package com.gymcrm.trainer.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.trainer.application.port.input.UpdateTrainerCommand;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerUpdateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("first_name")
	String firstName;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("last_name")
	String lastName;

	String specialization;

	@JsonProperty("is_active")
	@NotNull
	Boolean isActive;

	public UpdateTrainerCommand toCommand(UUID trainerId) {
		return new UpdateTrainerCommand(trainerId, firstName, lastName, specialization, isActive);
	}
}
