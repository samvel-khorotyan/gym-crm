package com.gymcrm.trainer.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.trainer.application.port.input.CreateTrainerCommand;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrainerCreateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("first_name")
	private String firstName;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("last_name")
	private String lastName;

	private String specialization;

	public CreateTrainerCommand toCommand() {
		return new CreateTrainerCommand(firstName, lastName, specialization);
	}
}
