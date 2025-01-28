package com.gymcrm.trainer.adapter.input.web.response;

import com.gymcrm.trainer.domain.Trainer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerLightResponse extends RepresentationModel<TrainerUserDetailsResponse> {
	private String firstName;
	private String lastName;

	public static TrainerLightResponse from(Trainer trainer) {
		return new TrainerLightResponse(trainer.getUser().getFirstName(), trainer.getUser().getLastName());
	}
}
