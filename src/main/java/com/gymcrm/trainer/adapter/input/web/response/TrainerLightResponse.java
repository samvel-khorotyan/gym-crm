package com.gymcrm.trainer.adapter.input.web.response;

import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.util.PasswordStorage;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrainerLightResponse extends RepresentationModel<TrainerUserDetailsResponse> {
	private String username;
	private String password;

	public static TrainerLightResponse from(Trainer trainer) {
		UUID userId = trainer.getUser().getId();
		String password = PasswordStorage.getPassword(userId);
		PasswordStorage.removePassword(userId);
		return new TrainerLightResponse(trainer.getUser().getUsername(), password);
	}
}
