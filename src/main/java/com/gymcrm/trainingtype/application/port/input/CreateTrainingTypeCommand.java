package com.gymcrm.trainingtype.application.port.input;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTrainingTypeCommand {
  String trainingTypeName;
}
