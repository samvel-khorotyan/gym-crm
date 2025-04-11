package com.gymcrm.user.application.port.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserCommand {
	String firstName;
	String lastName;
	Boolean isActive;
}
