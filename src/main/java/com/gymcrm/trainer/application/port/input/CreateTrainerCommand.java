package com.gymcrm.trainer.application.port.input;

import com.gymcrm.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTrainerCommand {
	String firstName;
	String lastName;
	String specialization;
	User user;

	public CreateTrainerCommand(String firstName, String lastName, String specialization) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.specialization = specialization;
	}
}
