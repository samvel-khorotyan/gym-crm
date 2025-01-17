package com.gymcrm.trainingtype.application;

import com.gymcrm.trainingtype.application.port.input.LoadTrainingTypeUseCase;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrainingTypeService implements LoadTrainingTypeUseCase {
  private final LoadTrainingTypePort loadTrainingTypePort;

  @Autowired
  public TrainingTypeService(LoadTrainingTypePort loadTrainingTypePort) {
    this.loadTrainingTypePort = loadTrainingTypePort;
  }

  @Override
  public List<TrainingType> loadAll() {
    return loadTrainingTypePort.findAll();
  }
}
