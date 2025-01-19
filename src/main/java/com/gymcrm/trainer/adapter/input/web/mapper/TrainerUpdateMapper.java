package com.gymcrm.trainer.adapter.input.web.mapper;

import com.gymcrm.trainer.application.port.input.UpdateTrainerCommand;
import com.gymcrm.trainer.domain.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TrainerUpdateMapper {
	void updateTrainerFromCommand(UpdateTrainerCommand command, @MappingTarget Trainer trainer);
}
