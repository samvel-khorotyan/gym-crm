package com.gymcrm.trainer.application.port.input;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivateDeactivateTrainerCommand {
	private String username;
	private Boolean isActive;
}
