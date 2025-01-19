package com.gymcrm.trainee.adapter.input.web.response;

import com.gymcrm.trainee.domain.Trainee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeLightResponse extends RepresentationModel<TraineeLightResponse> {
  private String username;
  private String password;

  public static TraineeLightResponse from(Trainee trainee) {
    return new TraineeLightResponse(
        trainee.getUser().getUsername(), trainee.getUser().getPassword());
  }
}
