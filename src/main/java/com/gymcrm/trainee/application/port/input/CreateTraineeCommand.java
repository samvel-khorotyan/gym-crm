package com.gymcrm.trainee.application.port.input;

import com.gymcrm.user.domain.User;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTraineeCommand {
	String firstName;
	String lastName;
	LocalDate dateOfBirth;
	String address;
	User user;

	public CreateTraineeCommand(String firstName, String lastName, LocalDate dateOfBirth, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.address = address;
	}
}
