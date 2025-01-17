package com.gymcrm.trainee.adapter.input.web.response;

import com.gymcrm.trainee.domain.Trainee;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TraineeLightResponse {
  String username;
  String password;

  public static TraineeLightResponse from(Trainee trainee) {
    return new TraineeLightResponse(
        trainee.getUser().getUsername(), trainee.getUser().getPassword());
  }
}
