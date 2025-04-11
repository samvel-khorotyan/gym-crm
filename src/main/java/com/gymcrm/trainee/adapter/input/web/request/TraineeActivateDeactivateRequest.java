package com.gymcrm.trainee.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.trainee.application.port.input.ActivateDeactivateTraineeCommand;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeActivateDeactivateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	private String username;

	@NotNull
	@JsonProperty("is_active")
	private Boolean isActive;

	public ActivateDeactivateTraineeCommand toCommand() {
		return new ActivateDeactivateTraineeCommand(username, isActive);
	}
}
