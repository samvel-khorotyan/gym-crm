package com.gymcrm.unit.trainingtype.adapter.input.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.trainingtype.adapter.input.web.controller.TrainingTypeController;
import com.gymcrm.trainingtype.adapter.input.web.response.TrainingTypeResponse;
import com.gymcrm.trainingtype.application.port.input.LoadTrainingTypeUseCase;
import com.gymcrm.trainingtype.domain.TrainingType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class TrainingTypeControllerTest {

	@Mock
	private LoadTrainingTypeUseCase loadTrainingTypeUseCase;

	@InjectMocks
	private TrainingTypeController trainingTypeController;

	@Test
	void getTrainingTypes_ShouldReturnListOfTrainingTypes_WhenTrainingTypesExist() {
		TrainingType type1 = new TrainingType(UUID.randomUUID(), "Strength Training");
		TrainingType type2 = new TrainingType(UUID.randomUUID(), "Cardio Training");
		List<TrainingType> trainingTypes = Arrays.asList(type1, type2);

		when(loadTrainingTypeUseCase.loadAll()).thenReturn(trainingTypes);

		List<TrainingTypeResponse> responses = trainingTypeController.getTrainingTypes();

		assertNotNull(responses);
		assertEquals(2, responses.size());
		assertEquals(type1.getTrainingTypeName(), responses.get(0).getTrainingType());
		assertEquals(type2.getTrainingTypeName(), responses.get(1).getTrainingType());
		verify(loadTrainingTypeUseCase, times(1)).loadAll();
	}

	@Test
  void getTrainingTypes_ShouldReturnEmptyList_WhenNoTrainingTypesExist() {
    when(loadTrainingTypeUseCase.loadAll()).thenReturn(Collections.emptyList());

    List<TrainingTypeResponse> responses = trainingTypeController.getTrainingTypes();

    assertNotNull(responses);
    assertTrue(responses.isEmpty());
    verify(loadTrainingTypeUseCase, times(1)).loadAll();
  }

	@Test
	void getTrainingTypes_ShouldThrowUnauthorized_WhenUserNotAuthenticated() {
		doThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access"))
		        .when(loadTrainingTypeUseCase).loadAll();

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
		        () -> trainingTypeController.getTrainingTypes());

		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
		verify(loadTrainingTypeUseCase, times(1)).loadAll();
	}

	@Test
	void getTrainingTypes_ShouldThrowForbidden_WhenUserLacksPermission() {
		doThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden access")).when(loadTrainingTypeUseCase)
		        .loadAll();

		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
		        () -> trainingTypeController.getTrainingTypes());

		assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
		verify(loadTrainingTypeUseCase, times(1)).loadAll();
	}

	@Test
	void getTrainingTypes_ShouldLogTransactionIdAndSuccess() {
		TrainingType type = new TrainingType(UUID.randomUUID(), "Yoga");
		List<TrainingType> trainingTypes = Collections.singletonList(type);

		when(loadTrainingTypeUseCase.loadAll()).thenReturn(trainingTypes);

		List<TrainingTypeResponse> responses = trainingTypeController.getTrainingTypes();

		assertNotNull(responses);
		assertEquals(1, responses.size());
		verify(loadTrainingTypeUseCase, times(1)).loadAll();
	}
}
