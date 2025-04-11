package com.gymcrm.trainee.adapter.input.web.response;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.adapter.input.web.response.TrainerDetailsResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeResponse extends RepresentationModel<TraineeResponse> {
	private String username;
	private String firstName;
	private String lastName;
	private String dateOfBirth;
	private String address;
	private boolean isActive;
	private List<TrainerDetailsResponse> trainersList;

	public static TraineeResponse from(Trainee trainee) {
		return new TraineeResponse(trainee.getUser().getUsername(), trainee.getUser().getFirstName(),
		        trainee.getUser().getLastName(),
		        trainee.getDateOfBirth() != null ? trainee.getDateOfBirth().toString() : null, trainee.getAddress(),
		        trainee.getUser().getIsActive(), TrainerDetailsResponse.from(trainee.getTrainers()));
	}
}
