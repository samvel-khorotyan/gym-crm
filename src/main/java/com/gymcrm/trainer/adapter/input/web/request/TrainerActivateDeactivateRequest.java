package com.gymcrm.trainer.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.trainer.application.port.input.ActivateDeactivateTrainerCommand;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerActivateDeactivateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	private String username;

	@NotNull
	@JsonProperty("is_active")
	private Boolean isActive;

	public ActivateDeactivateTrainerCommand toCommand() {
		return new ActivateDeactivateTrainerCommand(username, isActive);
	}
}
