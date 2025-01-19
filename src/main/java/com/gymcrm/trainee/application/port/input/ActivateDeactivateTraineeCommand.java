package com.gymcrm.trainee.application.port.input;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivateDeactivateTraineeCommand {
	private String username;
	private Boolean isActive;
}
