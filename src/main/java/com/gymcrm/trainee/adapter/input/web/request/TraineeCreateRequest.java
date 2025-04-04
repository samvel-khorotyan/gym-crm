package com.gymcrm.trainee.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeCreateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("first_name")
	private String firstName;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("last_name")
	private String lastName;

	@JsonProperty("date_of_birth")
	private LocalDate dateOfBirth;

	private String address;

	public CreateTraineeCommand toCommand() {
		return new CreateTraineeCommand(firstName, lastName, dateOfBirth, address);
	}
}
