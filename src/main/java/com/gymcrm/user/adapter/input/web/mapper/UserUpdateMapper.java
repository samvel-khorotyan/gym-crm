package com.gymcrm.user.adapter.input.web.mapper;

import com.gymcrm.user.application.port.input.UpdateUserCommand;
import com.gymcrm.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserUpdateMapper {
  void updateUserFromCommand(UpdateUserCommand command, @MappingTarget User trainee);
}
