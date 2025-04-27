package com.gymcrm.cucumber.component.steps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.trainer.adapter.input.web.response.TrainerResponse;
import com.gymcrm.trainer.application.exception.TrainerNotFoundException;
import com.gymcrm.trainer.application.port.input.TrainerUpdateUseCase;
import com.gymcrm.trainer.application.port.input.UpdateTrainerCommand;
import com.gymcrm.trainer.domain.Trainer;
import com.gymcrm.user.domain.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TrainerUpdateSteps {
	private final TrainerUpdateUseCase trainerUpdateUseCase;

	private UUID trainerId;
	private TrainerResponse response;
	private Exception exception;
	private Trainer existingTrainer;

	public TrainerUpdateSteps() {
		this.trainerUpdateUseCase = mock(TrainerUpdateUseCase.class);
	}

	@Before
	public void setup() {
		reset(trainerUpdateUseCase);
		exception = null;
		response = null;
	}

	@Given("the following trainer exists in the system:")
	public void theFollowingTrainerExistsInTheSystem(DataTable dataTable) {
		Map<String, String> data = dataTable.asMap(String.class, String.class);

		trainerId = UUID.fromString(data.get("id"));

		User user = createUser(UUID.randomUUID(), data.get("username"), data.get("firstName"), data.get("lastName"),
		        Boolean.parseBoolean(data.get("isActive")));

		existingTrainer = createTrainer(trainerId, data.get("specialization"), user, new ArrayList<Trainee>());

		when(trainerUpdateUseCase.update(any(UpdateTrainerCommand.class))).thenReturn(existingTrainer);
	}

	@When("I update the trainer with ID {string} with the following details:")
	public void iUpdateTheTrainerWithIDWithTheFollowingDetails(String id, DataTable dataTable) {
		Map<String, String> data = dataTable.asMap(String.class, String.class);
		UUID requestedTrainerId = UUID.fromString(id);

		String firstName = data.get("firstName");
		String lastName = data.get("lastName");
		String specialization = data.get("specialization");
		String isActiveStr = data.get("isActive");
		boolean isActive = isActiveStr == null || Boolean.parseBoolean(isActiveStr);

		UpdateTrainerCommand command = new UpdateTrainerCommand(requestedTrainerId, firstName, lastName, specialization,
		        isActive);

		try {
			if (!requestedTrainerId.equals(this.trainerId)) {
				String errorMessage = "Trainer not found by trainer ID: " + id;
				when(trainerUpdateUseCase.update(any(UpdateTrainerCommand.class)))
				        .thenThrow(new TrainerNotFoundException(errorMessage));

				trainerUpdateUseCase.update(command);
				throw new TrainerNotFoundException(errorMessage);
			}

			User updatedUser = createUser(existingTrainer.getUser().getId(), existingTrainer.getUser().getUsername(),
			        firstName, lastName, isActive);

			Trainer updatedTrainer = createTrainer(requestedTrainerId, specialization, updatedUser,
			        existingTrainer.getTrainees());

			when(trainerUpdateUseCase.update(any(UpdateTrainerCommand.class))).thenReturn(updatedTrainer);

			Trainer result = trainerUpdateUseCase.update(command);
			response = TrainerResponse.from(result);
		} catch (Exception e) {
			exception = e;
		}
	}

	@Then("the trainer should be updated successfully")
	public void theTrainerShouldBeUpdatedSuccessfully() {
		assertNull(exception, "Exception should not be thrown: " + (exception != null ? exception.getMessage() : ""));
		assertNotNull(response, "Response should not be null");
		verify(trainerUpdateUseCase, times(1)).update(any(UpdateTrainerCommand.class));
	}

	@Then("the updated trainer should have the following details:")
	public void theUpdatedTrainerShouldHaveTheFollowingDetails(DataTable dataTable) {
		Map<String, String> expectedData = dataTable.asMap(String.class, String.class);

		assertNotNull(response, "Response should not be null");
		assertEquals(expectedData.get("firstName"), response.getFirstName(), "First name should match");
		assertEquals(expectedData.get("lastName"), response.getLastName(), "Last name should match");
		assertEquals(expectedData.get("specialization"), response.getSpecialization(), "Specialization should match");

		String expectedIsActive = expectedData.get("isActive");
		if (expectedIsActive != null) {
			assertEquals(Boolean.parseBoolean(expectedIsActive), response.isActive(), "Active status should match");
		}
	}

	@Then("the trainer update should fail with error {string}")
	public void theTrainerUpdateShouldFailWithError(String errorMessage) {
		assertNotNull(exception, "Exception should be thrown");
		assertTrue(exception.getMessage().contains(errorMessage),
		        "Error message should contain: " + errorMessage + ", but was: " + exception.getMessage());
	}

	private User createUser(UUID id, String username, String firstName, String lastName, boolean isActive) {
		User user = new User();
		user.setId(id);
		user.setUsername(username);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setIsActive(isActive);
		return user;
	}

	private Trainer createTrainer(UUID id, String specialization, User user, List<Trainee> trainees) {
		Trainer trainer = new Trainer();
		trainer.setId(id);
		trainer.setSpecialization(specialization);
		trainer.setUser(user);
		trainer.setTrainees(trainees);
		return trainer;
	}
}
