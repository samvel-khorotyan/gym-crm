package com.gymcrm.trainee.adapter.input.web.response;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.util.PasswordStorage;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeLightResponse extends RepresentationModel<TraineeLightResponse> {
	private String username;
	private String password;

	public static TraineeLightResponse from(Trainee trainee) {
		UUID userId = trainee.getUser().getId();
		String password = PasswordStorage.getPassword(userId);
		PasswordStorage.removePassword(userId);
		return new TraineeLightResponse(trainee.getUser().getUsername(), password);
	}
}
