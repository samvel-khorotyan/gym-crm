package com.gymcrm.unit.trainingtype.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainingtype.application.TrainingTypeService;
import com.gymcrm.trainingtype.application.port.output.LoadTrainingTypePort;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceTest {
	@Mock
	private LoadTrainingTypePort loadTrainingTypePort;

	@InjectMocks
	private TrainingTypeService trainingTypeService;

	@Test
	void loadAll_ShouldReturnAllTrainingTypes() {
		List<TrainingType> trainingTypes = List.of(new TrainingType(), new TrainingType());
		when(loadTrainingTypePort.findAll()).thenReturn(trainingTypes);

		List<TrainingType> result = trainingTypeService.loadAll();

		assertEquals(trainingTypes.size(), result.size());
		verify(loadTrainingTypePort, times(1)).findAll();
	}

	@Test
	void loadAll_ShouldLogTransactionIdAndReturnResults() {
		MDC.put("transactionId", "12345");
		List<TrainingType> trainingTypes = List.of(new TrainingType());
		when(loadTrainingTypePort.findAll()).thenReturn(trainingTypes);

		List<TrainingType> result = trainingTypeService.loadAll();

		assertEquals(1, result.size());
		assertEquals(trainingTypes, result);
		verify(loadTrainingTypePort, times(1)).findAll();

		MDC.clear();
	}

	@Test
	void loadAll_ShouldThrowException_WhenLoadTrainingTypePortFails() {
		MDC.put("transactionId", "12345");
		when(loadTrainingTypePort.findAll()).thenThrow(new RuntimeException("Database error"));

		RuntimeException exception = assertThrows(RuntimeException.class, trainingTypeService::loadAll);
		assertEquals("Failed to fetch training types.", exception.getMessage());
		verify(loadTrainingTypePort, times(1)).findAll();

		MDC.clear();
	}
}
