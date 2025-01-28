package com.gymcrm.unit.training.adapter.input.web.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.training.adapter.input.web.controller.TrainingController;
import com.gymcrm.training.adapter.input.web.request.TrainingCreateRequest;
import com.gymcrm.training.application.port.input.TrainingCreationUseCase;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {
	@Mock
	private TrainingCreationUseCase trainingCreationUseCase;

	@InjectMocks
	private TrainingController trainingController;

	@Test
	void create_ShouldCallUseCase_WhenRequestIsValid() {
		TrainingCreateRequest request = new TrainingCreateRequest("traineeUser123", "trainerUser123", "Morning Yoga",
		        LocalDate.of(2025, 1, 20), 60);

		assertDoesNotThrow(() -> trainingController.create(request));

		verify(trainingCreationUseCase, times(1)).create(request.toCommand());
	}

	@Test
	void create_ShouldThrowException_WhenUseCaseThrowsException() {
		TrainingCreateRequest request = new TrainingCreateRequest("traineeUser123", "trainerUser123", "Morning Yoga",
		        LocalDate.of(2025, 1, 20), 60);

		doThrow(new RuntimeException("Unexpected error")).when(trainingCreationUseCase).create(request.toCommand());

		RuntimeException exception = assertThrows(RuntimeException.class, () -> trainingController.create(request));

		assertEquals("Unexpected error", exception.getMessage());
		verify(trainingCreationUseCase, times(1)).create(request.toCommand());
	}
}
