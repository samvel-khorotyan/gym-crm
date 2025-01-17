package com.gymcrm.trainee.adapter.input.web.mapper;

import com.gymcrm.trainee.application.port.input.UpdateTraineeCommand;
import com.gymcrm.trainee.domain.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TraineeUpdateMapper {
  void updateTraineeFromCommand(UpdateTraineeCommand command, @MappingTarget Trainee trainee);
}
