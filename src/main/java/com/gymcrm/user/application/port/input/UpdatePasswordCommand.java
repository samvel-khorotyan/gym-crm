package com.gymcrm.user.application.port.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordCommand {
	private String Username;
	private String oldPassword;
	private String newPassword;
}
