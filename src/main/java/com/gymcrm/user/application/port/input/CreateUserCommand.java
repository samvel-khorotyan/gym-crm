package com.gymcrm.user.application.port.input;

import com.gymcrm.user.domain.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateUserCommand {
	String firstName;
	String lastName;
	String username;
	String password;
	UserType userType;

	public CreateUserCommand(String firstName, String lastName, UserType userType) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.userType = userType;
	}
}
