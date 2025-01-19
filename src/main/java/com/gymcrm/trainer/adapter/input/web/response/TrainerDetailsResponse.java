package com.gymcrm.trainer.adapter.input.web.response;

import com.gymcrm.trainer.domain.Trainer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerDetailsResponse extends RepresentationModel<TrainerUserDetailsResponse> {
	private String username;
	private String firstName;
	private String lastName;
	private String specialization;

	public static TrainerDetailsResponse from(Trainer trainer) {
		return new TrainerDetailsResponse(trainer.getUser().getUsername(), trainer.getUser().getFirstName(),
		        trainer.getUser().getLastName(), trainer.getSpecialization());
	}

	public static List<TrainerDetailsResponse> from(List<Trainer> trainees) {
		return trainees == null
		        ? Collections.emptyList()
		        : trainees.stream().map(TrainerDetailsResponse::from).collect(Collectors.toList());
	}
}
