package com.gymcrm.user.application.port.input;

import com.gymcrm.user.domain.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserCommand {
  String firstName;
  String lastName;
  String username;
  String password;
  UserType userType;

  public CreateUserCommand(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }
}
