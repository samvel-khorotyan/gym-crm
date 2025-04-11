package com.gymcrm.trainee.application.factory;

import com.gymcrm.common.UUIDGeneratorInterface;
import com.gymcrm.trainee.application.port.input.CreateTraineeCommand;
import com.gymcrm.trainee.domain.Trainee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineeFactory {
	private final UUIDGeneratorInterface uuidGenerator;

	public Trainee createFrom(CreateTraineeCommand command) {
		return new Trainee(uuidGenerator.newUUID(), command.getDateOfBirth(), command.getAddress(), command.getUser());
	}
}
