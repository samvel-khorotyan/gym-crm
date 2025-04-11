package com.gymcrm.trainer.application.factory;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainer.application.port.input.CreateTrainerCommand;
import com.gymcrm.trainer.domain.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerFactory {
	private final UUIDGeneratorInterface uuidGenerator;

	public Trainer createFrom(CreateTrainerCommand command) {
		return new Trainer(uuidGenerator.newUUID(), command.getSpecialization(), command.getUser());
	}
}
