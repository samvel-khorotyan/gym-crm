package com.gymcrm.trainee.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.trainee.application.port.input.UpdateTraineeCommand;
import java.time.LocalDate;
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
public class TraineeUpdateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("first_name")
	String firstName;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("last_name")
	String lastName;

	@JsonProperty("date_of_birth")
	LocalDate dateOfBirth;

	String address;

	@JsonProperty("is_active")
	@NotNull
	Boolean isActive;

	public UpdateTraineeCommand toCommand(UUID traineeId) {
		return new UpdateTraineeCommand(traineeId, firstName, lastName, dateOfBirth, address, isActive);
	}
}
