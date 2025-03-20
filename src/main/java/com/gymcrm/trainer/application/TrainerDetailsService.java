package com.gymcrm.trainer.application;

import com.gymcrm.trainer.adapter.input.web.response.TrainerDetailsWithWorkloadResponse;
import com.gymcrm.trainer.adapter.input.web.response.TrainerMonthlyWorkloadResponse;
import com.gymcrm.trainer.application.port.input.LoadTrainerDetailsUseCase;
import com.gymcrm.trainer.application.port.input.LoadTrainerWorkloadUseCase;
import com.gymcrm.trainer.application.port.output.LoadTrainerPort;
import com.gymcrm.trainer.domain.Trainer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TrainerDetailsService implements LoadTrainerDetailsUseCase {
	private final LoadTrainerWorkloadUseCase loadTrainerWorkloadUseCase;
	private final LoadTrainerPort loadTrainerPort;

	@Override
	public TrainerDetailsWithWorkloadResponse loadTrainerDetailsWithWorkload(String username) {
		Trainer trainer = loadTrainerPort.findByUsername(username);
		TrainerMonthlyWorkloadResponse workload = loadTrainerWorkloadUseCase.loadTrainerCurrentMonthWorkload(username);

		return TrainerDetailsWithWorkloadResponse.builder().username(trainer.getUser().getUsername())
		        .firstName(trainer.getUser().getFirstName()).lastName(trainer.getUser().getLastName())
		        .specialization(trainer.getSpecialization()).isActive(trainer.getUser().getIsActive())
		        .currentMonthWorkload(workload.getSummaryDuration()).build();
	}
}
